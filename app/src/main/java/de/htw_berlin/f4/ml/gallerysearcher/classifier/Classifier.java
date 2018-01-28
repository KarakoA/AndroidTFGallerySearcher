package de.htw_berlin.f4.ml.gallerysearcher.classifier;

import android.graphics.Bitmap;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Generic interface for interacting with different recognition engines.
 */
public interface Classifier {
    /**
     * An immutable result returned by a Classifier describing what was recognized.
     */
    class Recognition {
        /**
         * The target class id.
         */
        private final String id;

        /**
         * Display name for the recognition.
         */
        private final String title;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;

        public Recognition(
                final String id, final String title, final Float confidence) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null)
                resultString += "[" + id + "] ";

            if (title != null)
                resultString += title + " ";

            if (confidence != null)
                resultString += String.format(Locale.ENGLISH, "(%.1f%%) ", confidence * 100.0f);

            return resultString.trim();
        }

        public static Comparator<Recognition> reverseConfidenceComparator = new Comparator<Recognition>() {
            @Override
            public int compare(Recognition left, Recognition right) {
                // Intentionally reversed to put those with a high confidence at the head
                return Float.compare(right.getConfidence(), left.getConfidence());
            }
        };
    }

    /**
     * Recognizes the given bitmap image.
     *
     * @param bitmap the bitmap image to recognize
     * @return a list of recognitions
     */
    List<Recognition> recognizeImage(Bitmap bitmap);

    /**
     * Closes the model and frees up resources.
     */
    void close();
}

