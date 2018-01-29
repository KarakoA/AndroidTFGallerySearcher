package de.htw_berlin.f4.ml.gallerysearcher.classifier;


import android.content.res.AssetManager;
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageClassifierWithCaching implements Classifier {

    //singleton instance
    private static Classifier sInstance;

    /**
     * Gets or creates a classifier with the default parameters.
     */
    public static Classifier getInstance(AssetManager assetManager) {
        synchronized (Classifier.class) {
            if (sInstance == null)
                sInstance = new ImageClassifierWithCaching(assetManager);
            return sInstance;
        }
    }

    private Classifier tfClassifier;

    private ImageClassifierWithCaching(AssetManager assetManager) {
        tfClassifier = TensorFlowImageClassifier.getInstance(assetManager);

        cache = new HashMap<>();
    }

    private Map<Integer, List<Recognition>> cache;

    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) {
        int key = bitmap.hashCode();

        if (cache.containsKey(key))
            return cache.get(key);

        List<Recognition> result = tfClassifier.recognizeImage(bitmap);
        cache.put(key, result);
        return result;
    }

    @Override
    public void close() {
        tfClassifier.close();
    }
}
