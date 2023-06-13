package com.mrichard.napkin_handler.ui.adapter;

import android.app.Activity;
import android.content.Context;

import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.ui.dashboard.DashboardViewModel;

import java.util.Set;

public class PictureGalleryAdapter extends PictureGalleryAdapterBase {

    private Set<Long> selectedPictures;

    public PictureGalleryAdapter(Context context, Activity activity, DashboardViewModel dashboardViewModel) {
        super(context, activity, dashboardViewModel);
    }

    public void setSelectedPictures(Set<Long> selectedPictures) {
        this.selectedPictures = selectedPictures;
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
