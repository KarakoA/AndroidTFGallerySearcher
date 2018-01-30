package de.htw_berlin.f4.ml.gallerysearcher.activities;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.tensorflow.TensorFlow;
import org.tensorflow.TensorFlowException;

import de.htw_berlin.f4.ml.gallerysearcher.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        
    }

    public void onNavigateToGalleryClicked(View view) {
        //create a new intent to navigate to the gallery activity
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }
}
