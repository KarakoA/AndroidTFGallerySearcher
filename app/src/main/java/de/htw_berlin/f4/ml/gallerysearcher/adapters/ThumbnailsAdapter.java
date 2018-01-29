package de.htw_berlin.f4.ml.gallerysearcher.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.htw_berlin.f4.ml.gallerysearcher.R;
import de.htw_berlin.f4.ml.gallerysearcher.services.ThumbnailsService;
import de.htw_berlin.f4.ml.gallerysearcher.classifier.Classifier;
import de.htw_berlin.f4.ml.gallerysearcher.classifier.ImageClassifierWithCaching;
import de.htw_berlin.f4.ml.gallerysearcher.databinding.ViewCellGalleryBinding;
import de.htw_berlin.f4.ml.gallerysearcher.models.ThumbnailsImageModel;
import de.htw_berlin.f4.ml.gallerysearcher.utils.Callback;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.ViewHolder> {
    private ThumbnailsImageModel[] models;
    Callback<Integer>  callback;
    private Classifier classifier;

    private ThumbnailsService thumbnailsService;

    public ThumbnailsAdapter(Context context,  Callback<Integer>  callback) {
        //get a cursor for all mini thumbnails. The mini thumbnails have the size 512 x 384
        classifier = ImageClassifierWithCaching.getInstance(context.getAssets());
        this.callback = callback;
        thumbnailsService = ThumbnailsService.getInstance(context.getContentResolver());

        //init models
        models = new ThumbnailsImageModel[getItemCount()];
        for (int i = 0; i < models.length; i++) {
            models[i] = new ThumbnailsImageModel();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewCellGalleryBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_cell_gallery, parent, false);
        return new ViewHolder(viewDataBinding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //get the model
        final ThumbnailsImageModel model = models[position];
        //set it for the view holder
        holder.viewCellGalleryBinding.setModel(model);

        //start a task to update the labels with tensorflow
        model.IniializeAsyncIfNecessary(classifier, thumbnailsService, position);

        //update bitmap
        Bitmap bitmap = thumbnailsService.getBitmapForPosition(position, MediaStore.Images.Thumbnails.MICRO_KIND);
        models[position].bitmap.set(bitmap);
        holder.viewCellGalleryBinding.imageView.setImageBitmap(bitmap);

        //set a callback to the activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageId = thumbnailsService.getImageIdFromPosition(position);
                callback.call(imageId);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return thumbnailsService.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ViewCellGalleryBinding viewCellGalleryBinding;

        ViewHolder(ViewCellGalleryBinding itemView) {
            super(itemView.getRoot());
            viewCellGalleryBinding = itemView;
        }
    }
}
