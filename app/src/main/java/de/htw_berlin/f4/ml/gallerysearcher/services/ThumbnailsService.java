package de.htw_berlin.f4.ml.gallerysearcher.services;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import de.htw_berlin.f4.ml.gallerysearcher.classifier.Classifier;

public class ThumbnailsService {
    //singleton instance
    private static ThumbnailsService sInstance;

    /**
     * Gets or creates a classifier with the default parameters.
     */
    public static ThumbnailsService getInstance(ContentResolver resolver) {
        synchronized (Classifier.class) {
            if (sInstance == null)
                sInstance = new ThumbnailsService(resolver);
            return sInstance;
        }
    }

    private ContentResolver resolver;
    private Cursor thumbnailsCursor;

    public ThumbnailsService(ContentResolver resolver) {
        //get a cursor for all mini thumbnails. The mini thumbnails have the size 512 x 384
        thumbnailsCursor = MediaStore.Images.Thumbnails.queryMiniThumbnails(resolver, MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, MediaStore.Images.Thumbnails.MINI_KIND, null);

       this.resolver = resolver;
    }

    /**
     * Returns the image id of the Thumbnail at the given index.
     *
     * @param position the index
     */
    public int getImageIdFromPosition(int position) {
        if (thumbnailsCursor.moveToPosition(position)) {
            int imageIdIndex = thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int imageId = thumbnailsCursor.getInt(imageIdIndex);
            return imageId;
        }
        return 0;
    }

    /**
     * Returns the bitmap for the item with the given index.
     *
     * @param position the index
     */
    public Bitmap getBitmapForPosition(int position, int thumbnailType) {
        //attempt to go to the position-th position
        if (thumbnailsCursor.moveToPosition(position)) {
            int originImageIdIndex = thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            // get the image id
            int originalImageId = thumbnailsCursor.getInt(originImageIdIndex);

            return MediaStore.Images.Thumbnails.getThumbnail(resolver, originalImageId, thumbnailType, null);
        } else return null;
    }

    public Bitmap getBitmapForImageId(int imageId,int thumbnailType){
        return MediaStore.Images.Thumbnails.getThumbnail(resolver,imageId,thumbnailType,null);
    }


    public int getCount() {
        return thumbnailsCursor == null ? 0 : thumbnailsCursor.getCount();
    }


}
