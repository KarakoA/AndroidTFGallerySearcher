/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package de.htw_berlin.f4.ml.gallerysearcher.classifier;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A classifier specialized to label images using TensorFlow.
 */
public class TensorFlowImageClassifier implements Classifier {

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    // Only return this many results with at least this confidence.
    private static final int MAX_RESULTS = 3;
    private static final float THRESHOLD = 0.1f;

    // Config values.
    private String inputName;
    private String outputName;
    private int inputSize;
    private int imageMean;
    private float imageStd;

    // Pre-allocated buffers.
    private ArrayList<String> labels = new ArrayList<String>();
    private String[] outputNames;

    private int numClasses;

    private TensorFlowInferenceInterface inferenceInterface;

    private TensorFlowImageClassifier() {
    }

    //singleton instance
    private static Classifier sInstance;

    /**
     * Gets or creates a classifier with the default parameters.
     */
    public static Classifier getInstance(AssetManager assetManager) {
        synchronized (Classifier.class) {
            if (sInstance == null)
                try {
                    sInstance = create(assetManager, MODEL_FILE, LABEL_FILE, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            return sInstance;
        }
    }

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager  The asset manager to be used to load assets.
     * @param modelFilename The filepath of the model GraphDef protocol buffer.
     * @param labelFilePath The filepath of label file for classes.
     * @param inputSize     The input size. A square image of inputSize x inputSize is assumed.
     * @param imageMean     The assumed mean of the image values.
     * @param imageStd      The assumed std of the image values.
     * @param inputName     The label of the image input node.
     * @param outputName    The label of the output node.
     * @throws IOException if an I/O related problem occurred
     */
    private static Classifier create(AssetManager assetManager, String modelFilename, String labelFilePath, int inputSize,
                                     int imageMean, float imageStd, String inputName, String outputName) throws IOException {

        TensorFlowImageClassifier classifier = new TensorFlowImageClassifier();
        classifier.inputName = inputName;
        classifier.outputName = outputName;

        //read the labels from the passed assets file
        classifier.labels = readLabelsFromAssetsFileOrThrow(assetManager, labelFilePath);

        //create the native tensorflow interface
        classifier.inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);

        // The shape of the output is [N, NUM_CLASSES], where N is the batch size.
        final Operation operation = classifier.inferenceInterface.graphOperation(outputName);
        classifier.numClasses = (int) operation.output(0).shape().size(1);
        System.out.println("Read " + classifier.labels.size() + " labels, output layer size is " + classifier.numClasses);

        classifier.inputSize = inputSize;
        classifier.imageMean = imageMean;
        classifier.imageStd = imageStd;

        classifier.outputNames = new String[]{outputName};
        return classifier;
    }


    /**
     * Reads the labels from the given file path in the assets directory.
     *
     * @param assetManager  the asset manager. Used to access the assets directory
     * @param labelFilePath the path to the label file
     * @return the labels as an array list
     * @throws IOException if an I/O related problem occured
     */
    private static ArrayList<String> readLabelsFromAssetsFileOrThrow(AssetManager assetManager, String labelFilePath) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        String actualFilename = labelFilePath.split("file:///android_asset/")[1];

        BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(actualFilename)));
        String line;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        br.close();

        return result;
    }

    private float[] normalizeDataAndWriteToBuffer(final Bitmap bitmap) {

        //to hold the pixel rgb values
        int[] intValues = new int[bitmap.getWidth() * bitmap.getHeight()];
        //to hold the normalized rgb values
        float[] floatValues = new float[bitmap.getWidth() * bitmap.getHeight() * 3];

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            //R
            floatValues[i * 3 + 0] = (((val >> 16) & 0xFF) - imageMean) / imageStd;
            //G
            floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - imageMean) / imageStd;
            //B
            floatValues[i * 3 + 2] = ((val & 0xFF) - imageMean) / imageStd;
        }
        return floatValues;
    }

    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) {
        synchronized (TensorFlowImageClassifier.class) {
            //scale to inputSize x inputSize
            bitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true);
            //normalize with mean - imageMean and variance imageStd
            float[] floatValues = normalizeDataAndWriteToBuffer(bitmap);

            // Copy the input data into TensorFlow.
            inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 3);

            // Run the data through the model
            inferenceInterface.run(outputNames);

            // Copy the output Tensor back into the output array.
            float[] outputs = new float[numClasses];
            inferenceInterface.fetch(outputName, outputs);

            ArrayList<Recognition> recognitions = getTopResultsByConfidence(outputs);
            return recognitions;
        }
    }

    private ArrayList<Recognition> getTopResultsByConfidence(float[] outputs) {
        // Find the best classifications.
        PriorityQueue<Recognition> pq = new PriorityQueue<Recognition>(3, Recognition.reverseConfidenceComparator);

        //add those above the threshold
        for (int i = 0; i < outputs.length; ++i) {
            if (outputs[i] > THRESHOLD) {
                pq.add(new Recognition("" + i, labels.size() > i ? labels.get(i) : "unknown", outputs[i]));
            }
        }
        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(pq.poll());
        }
        return recognitions;
    }

    @Override
    public void close() {
        inferenceInterface.close();
    }
}
