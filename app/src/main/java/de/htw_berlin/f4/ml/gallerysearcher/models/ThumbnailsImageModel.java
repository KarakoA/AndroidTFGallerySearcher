package de.htw_berlin.f4.ml.gallerysearcher.models;

import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.List;
import java.util.Locale;

import de.htw_berlin.f4.ml.gallerysearcher.services.ThumbnailsService;
import de.htw_berlin.f4.ml.gallerysearcher.classifier.Classifier;

public class ThumbnailsImageModel {
    public final ObservableField<List<Classifier.Recognition>> recognitions = new ObservableField<>();
    public final ObservableField<Bitmap> bitmap = new ObservableField<>();

    public final ObservableField<String> text = new ObservableField<>();

    public final ObservableField<Boolean> initialized = new ObservableField<>();


    public ThumbnailsImageModel() {
        initialized.set(false);
    }

    public void IniializeAsyncIfNecessary(Classifier classifier, ThumbnailsService thumbnailsService, int position) {
        if (initialized.get())
            return;
        new GetLabelsAsyncTask(this, classifier, thumbnailsService).execute(position);
        initialized.set(true);
    }

    private static class GetLabelsAsyncTask extends AsyncTask<Integer, Object, List<Classifier.Recognition>> {

        private Classifier classifier;
        private ThumbnailsImageModel model;
        private ThumbnailsService thumbnailsService;

        GetLabelsAsyncTask(ThumbnailsImageModel model, Classifier classifier, ThumbnailsService thumbnailsService) {
            this.model = model;
            this.classifier = classifier;
            this.thumbnailsService = thumbnailsService;
        }

        @Override
        protected void onPostExecute(List<Classifier.Recognition> recognitions) {
            model.recognitions.set(recognitions);
            StringBuilder stringBuilder = new StringBuilder();
            for (Classifier.Recognition recognition : recognitions) {
                stringBuilder.append(String.format(Locale.ENGLISH, "%s (%.1f%%)\n", recognition.getTitle(), recognition.getConfidence() * 100.0f));
            }
            model.text.set(stringBuilder.toString());
        }

        @Override
        protected List<Classifier.Recognition> doInBackground(Integer... positions) {
            Bitmap bitmap = thumbnailsService.getBitmapForPosition(positions[0], MediaStore.Images.Thumbnails.MINI_KIND);
            List<Classifier.Recognition> recognitions = classifier.recognizeImage(bitmap);
            return recognitions;
        }
    }
}

