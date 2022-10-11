package com.mrichard.napkin_handler.ui.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mrichard.napkin_handler.R;
import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.databinding.PictureGalleryItemBinding;
import com.mrichard.napkin_handler.ui.dashboard.DashboardViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureGalleryAdapter extends RecyclerView.Adapter<PictureGalleryViewHolder> {

    private Context context;

    private PictureGalleryItemBinding binding;

    private DashboardViewModel dashboardViewModel;

    private List<Picture> pictures;

    public PictureGalleryAdapter(Context context, DashboardViewModel dashboardViewModel) {
        this.context = context;

        this.dashboardViewModel = dashboardViewModel;

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

        // Clicking the picture.
        binding.imageViewPicture.setOnClickListener(view -> dashboardViewModel.onClickPicture(picture.getId()));
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

}
