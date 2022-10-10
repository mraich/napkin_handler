package com.mrichard.napkin_handler.ui.dashboard.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.mrichard.napkin_handler.databinding.FragmentDashboardBinding;
import com.mrichard.napkin_handler.databinding.PictureGalleryItemBinding;

public class PictureGalleryViewHolder extends RecyclerView.ViewHolder {

    private PictureGalleryItemBinding binding;

    public PictureGalleryViewHolder(PictureGalleryItemBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public PictureGalleryItemBinding getBinding() {
        return binding;
    }

}
