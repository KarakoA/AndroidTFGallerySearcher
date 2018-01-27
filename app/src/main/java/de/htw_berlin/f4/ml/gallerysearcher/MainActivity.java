package de.htw_berlin.f4.ml.gallerysearcher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onNavigateToGalleryClicked(View view) {
        //create a new intent to navigate to the gallery activity
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }
}
