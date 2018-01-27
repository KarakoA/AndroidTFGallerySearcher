package de.htw_berlin.f4.ml.gallerysearcher;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import de.htw_berlin.f4.ml.gallerysearcher.adapter.ThumnailsImageAdapater;
import de.htw_berlin.f4.ml.gallerysearcher.utils.Callback;

public class GalleryActivity extends AppCompatActivity implements Callback<Integer> {

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        //set the adapter for the content of the grid view
        GridView gridView = findViewById(R.id.gridView);
        ListAdapter adapter = new ThumnailsImageAdapater(getBaseContext(), this);
        gridView.setAdapter(adapter);
    }

    /**
     * Called when an image from the gallery is selected.
     */
    @Override
    public void call(Integer imageId) {
        Intent intent = new Intent(this, ImageFullscreenActivity.class);
        intent.putExtra(MediaStore.Images.Thumbnails.IMAGE_ID, imageId + "");
        startActivity(intent);
    }
}
