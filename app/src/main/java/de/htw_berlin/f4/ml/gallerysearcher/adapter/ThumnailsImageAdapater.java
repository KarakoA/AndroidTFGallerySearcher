package de.htw_berlin.f4.ml.gallerysearcher.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import static android.provider.MediaStore.Images.Thumbnails;

import de.htw_berlin.f4.ml.gallerysearcher.R;

public class ThumnailsImageAdapater extends BaseAdapter {
    private Context context;
    private Cursor thumbnailsCursor;
    private LayoutInflater inflater;

    public ThumnailsImageAdapater(Context context) {
        this.context = context;
        thumbnailsCursor = Thumbnails.queryMiniThumbnails(context.getContentResolver(), MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, Thumbnails.MINI_KIND, null);
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return thumbnailsCursor == null ? 0 : thumbnailsCursor.getCount();
    }

    @Override
    public Object getItem(int i) {
        return getBitmapForIndex(i);
    }

    private Bitmap getBitmapForIndex(int i) {
        if (thumbnailsCursor.moveToPosition(i)) {

            int originImageIdIndex = thumbnailsCursor.getColumnIndex(Thumbnails.IMAGE_ID);
            int originalImageId = thumbnailsCursor.getInt(originImageIdIndex);
            return Thumbnails.getThumbnail(context.getContentResolver(), originalImageId, Thumbnails.MICRO_KIND, null);
        } else return null;
    }

    @Override
    public long getItemId(int i) {
        return getIdOfItemAt(i);
    }

    private long getIdOfItemAt(int i) {
        if (thumbnailsCursor.moveToPosition(i))
            return thumbnailsCursor.getLong(thumbnailsCursor.getColumnIndex(Thumbnails._ID));
        else return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.view_cell_gallery_activity, viewGroup);
        }
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageBitmap((Bitmap) getItem(i));
        return view;
    }

}
