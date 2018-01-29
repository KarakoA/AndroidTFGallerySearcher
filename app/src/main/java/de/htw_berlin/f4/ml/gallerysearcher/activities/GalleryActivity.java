package de.htw_berlin.f4.ml.gallerysearcher.activities;


import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;

import de.htw_berlin.f4.ml.gallerysearcher.R;
import de.htw_berlin.f4.ml.gallerysearcher.adapters.ThumbnailsAdapter;
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

        GridLayoutManager layoutManager = new GridLayoutManager(getBaseContext(), 3);

        RecyclerView recyclerView = findViewById(R.id.contentView);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new ThumbnailsAdapter(getBaseContext(), this));
    }

    /**
     * Called when an image from the gallery is selected.
     */
    @Override
    public void call(Integer imageId) {
        Intent intent = new Intent(this, ImageFullscreenActivity.class);
        intent.putExtra(MediaStore.Images.Thumbnails.IMAGE_ID, imageId+"");

        startActivity(intent);
    }
}
