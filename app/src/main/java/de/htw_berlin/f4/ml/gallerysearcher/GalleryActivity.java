package de.htw_berlin.f4.ml.gallerysearcher;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.ListAdapter;

import de.htw_berlin.f4.ml.gallerysearcher.adapter.ThumnailsImageAdapater;

public class GalleryActivity extends AppCompatActivity {

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
        ListAdapter adapter = new ThumnailsImageAdapater(getBaseContext());
        gridView.setAdapter(adapter );

    }
}
