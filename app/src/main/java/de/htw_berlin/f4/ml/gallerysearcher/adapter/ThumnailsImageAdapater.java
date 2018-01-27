package de.htw_berlin.f4.ml.gallerysearcher.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import static android.provider.MediaStore.Images.Thumbnails;

import de.htw_berlin.f4.ml.gallerysearcher.GalleryActivity;
import de.htw_berlin.f4.ml.gallerysearcher.ImageFullscreenActivity;
import de.htw_berlin.f4.ml.gallerysearcher.R;
import de.htw_berlin.f4.ml.gallerysearcher.utils.Callback;

/**
 * A Adapter which displays the thumbnails of all images in the gallery of the phone.
 *
 * @author Anton K.
 */
public class ThumnailsImageAdapater extends BaseAdapter {
    /**
     * The app context.
     */
    private Context context;
    /**
     * The cursor to the thumbnails.
     */
    private Cursor thumbnailsCursor;
    /**
     * The layout inflater used to inflate the views.
     */
    private LayoutInflater inflater;

    /**
     * The callback which is called once an item is selected.
     */
    private Callback<Integer> mCallback;

    /**
     * Creates a new instance.
     *
     * @param context the app context.
     */
    public ThumnailsImageAdapater(Context context,Callback<Integer> callback) {
        this.context = context;
        //get a cursor for all mini thumbnails. The mini thumbnails have the size 512 x 384
        thumbnailsCursor = Thumbnails.queryMiniThumbnails(context.getContentResolver(), MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, Thumbnails.MINI_KIND, null);
        inflater = LayoutInflater.from(context);

        mCallback=callback;
    }

    /**
     * Returns the number of elements in the underlying collection.
     */
    @Override
    public int getCount() {
        return thumbnailsCursor == null ? 0 : thumbnailsCursor.getCount();
    }

    /**
     * Returns the bitmap of the i-th item.
     *
     * @param i the index
     */
    @Override
    public Object getItem(int i) {
        return getBitmapForIndex(i);
    }

    /**
     * Returns the bitmap for the item with the given index.
     *
     * @param i the index
     */
    private Bitmap getBitmapForIndex(int i) {
        //attempt to go to the i-th position
        if (thumbnailsCursor.moveToPosition(i)) {
            int originImageIdIndex = thumbnailsCursor.getColumnIndex(Thumbnails.IMAGE_ID);
            // get the image id
            int originalImageId = thumbnailsCursor.getInt(originImageIdIndex);
            //get the micro bitmap (94x94) for the image id
            return Thumbnails.getThumbnail(context.getContentResolver(), originalImageId, Thumbnails.MICRO_KIND, null);
        } else return null;
    }

    /**
     * Not used.
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Returns the image id of the Thumbnail at the given index.
     *
     * @param i the index
     */
    private int getImageIdFromIndex(int i) {
        if (thumbnailsCursor.moveToPosition(i)) {
            int imageIdIndex = thumbnailsCursor.getColumnIndex(Thumbnails.IMAGE_ID);
            int imageId = thumbnailsCursor.getInt(imageIdIndex);
            return imageId;
        }
        return 0;
    }

    /**
     * Creates a new view or recycles the given one with the new content at index i.
     *
     * @param i         the index if the new content
     * @param view      the view to recycle. Can be null
     * @param viewGroup the view Group
     */
    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        //if no view was created yet, inflate a new one from the layout xml file
        if (view == null) {
            view = inflater.inflate(R.layout.view_cell_gallery_activity, null);
        }
        //get the image view from the layout
        final ImageView imageView = view.findViewById(R.id.imageView);

        //set its bitmap
        imageView.setImageBitmap((Bitmap) getItem(i));

        //set a callback to the activity
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageId = getImageIdFromIndex(i);
                mCallback.call(imageId);
            }
        });

        return view;
    }
}
