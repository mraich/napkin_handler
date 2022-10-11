package com.mrichard.napkin_handler.ui.dashboard.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mrichard.napkin_handler.R;
import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.databinding.PictureGalleryItemBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PictureGalleryAdapter extends RecyclerView.Adapter<PictureGalleryViewHolder> {

    private Context context;

    private PictureGalleryItemBinding binding;

    private List<Picture> pictures;

    public PictureGalleryAdapter(Context context) {
        this.context = context;

        this.pictures = new ArrayList<>();
    }

    @Override
    public PictureGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            binding = PictureGalleryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new PictureGalleryViewHolder(binding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPictures(List<Picture> pictures)
    {
        this.pictures = pictures;
    }

    @Override
    public void onBindViewHolder(final PictureGalleryViewHolder holder, int position) {
        final Picture picture = pictures.get(position);

        Glide
            .with(context)
            .load(picture.getPath())
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.imageViewPicture);

        binding.imageViewClassified.setText(picture.getAttributes());

        // Sharing the picture.
        binding.imageViewPicture.setOnClickListener(view -> sharePictures(Collections.singletonList(picture)));
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    /**
     * Sharing the pictures.
     *
     * @param pictures
     */
    private void sharePictures(List<Picture> pictures) {
        ArrayList<Uri> pictureUris = new ArrayList<>();
        for (Picture picture: pictures) {
            pictureUris.add(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", new File(picture.getPath())));
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, pictureUris);

        context.startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
    }

}
