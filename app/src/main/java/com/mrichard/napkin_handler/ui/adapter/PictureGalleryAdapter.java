package com.mrichard.napkin_handler.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mrichard.napkin_handler.R;
import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.databinding.PictureGalleryItemBinding;
import com.mrichard.napkin_handler.ui.FontManager;
import com.mrichard.napkin_handler.ui.dashboard.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PictureGalleryAdapter extends RecyclerView.Adapter<PictureGalleryViewHolder> {

    private Context context;

    private Activity activity;

    private DashboardViewModel dashboardViewModel;

    private List<Picture> pictures;

    private Set<Long> selectedPictures;

    public PictureGalleryAdapter(Context context, Activity activity, DashboardViewModel dashboardViewModel) {
        this.context = context;

        this.activity = activity;

        this.dashboardViewModel = dashboardViewModel;

        this.pictures = new ArrayList<>();
    }

    @Override
    public PictureGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            PictureGalleryItemBinding binding = PictureGalleryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            FontManager.MarkAsIconContainer(binding.getRoot(), FontManager.FONT_AWESOME);

            return new PictureGalleryViewHolder(binding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPictures(List<Picture> pictures)
    {
        this.pictures = pictures;

        notifyDataSetChanged();
    }

    public void setSelectedPictures(Set<Long> selectedPictures) {
        this.selectedPictures = selectedPictures;
    }

    @Override
    public void onBindViewHolder(final PictureGalleryViewHolder holder, int position) {
        final Picture picture = pictures.get(position);

        showSelection(holder, picture);

        Glide
            .with(context)
            .load(picture.getPath())
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.getBinding().imageViewPicture);

        holder.getBinding().imageCountTextView.setText("" + picture.getCount());
        holder.getBinding().imageViewClassified.setText("" + picture.getSimilarity() + ", " + picture.getAttributesJson());

        // Clicking the picture.
        holder.getBinding().imageViewPicture.setOnClickListener(view -> {
            dashboardViewModel.onClickPicture(picture.getId());

            showSelection(holder, picture);
        });
        // Clicking the plus.
        holder.getBinding().imagePlusButton.setOnClickListener(view -> {
            picture.setCount(picture.getCount() + 1);

            // Saving.
            Thread thread = new Thread(() -> {
                NapkinDB.GetInstance(view.getContext()).pictureDao().update(picture);

                activity.runOnUiThread(() -> notifyItemChanged(position));
            });
            thread.start();
        });
        // Clicking the minus.
        holder.getBinding().imageMinusButton.setOnClickListener(view -> {
            picture.setCount(picture.getCount() - 1);

            // We cannot have negative amount of napkins.
            if (picture.getCount() < 0) {
                picture.setCount(0);

                return;
            }

            // Saving.
            Thread thread = new Thread(() -> {
                NapkinDB.GetInstance(view.getContext()).pictureDao().update(picture);

                activity.runOnUiThread(() -> notifyItemChanged(position));
            });
            thread.start();
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    // Showing the selection of a picture.
    private void showSelection(PictureGalleryViewHolder holder, Picture picture) {
        if (selectedPictures != null && selectedPictures.contains(picture.getId())) {
            holder.getBinding().getRoot().setBackgroundColor(context.getResources().getColor(com.google.android.material.R.color.material_blue_grey_800));
        } else {
            holder.getBinding().getRoot().setBackgroundColor(context.getResources().getColor(com.google.android.material.R.color.m3_ref_palette_white));
        }
    }

}
