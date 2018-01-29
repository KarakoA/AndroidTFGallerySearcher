package de.htw_berlin.f4.ml.gallerysearcher.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.htw_berlin.f4.ml.gallerysearcher.R;
import de.htw_berlin.f4.ml.gallerysearcher.classifier.Classifier;
import de.htw_berlin.f4.ml.gallerysearcher.classifier.ImageClassifierWithCaching;
import de.htw_berlin.f4.ml.gallerysearcher.services.ThumbnailsService;

/**
 * A full-screen activity that hides the system UI (i.e.
 * status bar and navigation/system bar)
 */
public class ImageFullscreenActivity extends AppCompatActivity {

    private View mContentView;

    private View mControlsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_fullscreen);

        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        //get the image id from the intent
        String imageId = getIntent().getStringExtra(MediaStore.Images.Thumbnails.IMAGE_ID);
        Bitmap bitmapMini = ThumbnailsService.getInstance(getContentResolver()).getBitmapForImageId(Integer.parseInt(imageId), MediaStore.Images.Thumbnails.MINI_KIND);
        List<Classifier.Recognition> recognitionList = ImageClassifierWithCaching.getInstance(getAssets()).recognizeImage(bitmapMini);
        StringBuilder stringBuilder = new StringBuilder();
        for (Classifier.Recognition recognition : recognitionList) {
            stringBuilder.append(String.format(Locale.ENGLISH, "%s (%.1f%%) ", recognition.getTitle(), recognition.getConfidence() * 100.0f));
        }

        String recognizedLabels = stringBuilder.toString();
        try {
            //set its source
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId));
            ((ImageView) mContentView).setImageBitmap(bitmap);
            TextView textView = mControlsView.findViewById(R.id.textView);
            textView.setText(recognizedLabels);
        } catch (IOException e) {
            //do nothing
        }

        hideSystemUI();
    }

    // This snippet hides the system bars.
    //https://developer.android.com/training/system-ui/immersive.html
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mContentView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}