package com.mrichard.napkin_handler.ui.adapter;

import android.app.Activity;
import android.content.Context;

import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.data.viewmodel.NapkinSelectorViewModel;

import java.util.Set;

public class PictureGalleryAdapter extends PictureGalleryAdapterBase {

    public PictureGalleryAdapter(Context context, Activity activity, NapkinSelectorViewModel napkinSelectorViewModel) {
        super(context, activity, napkinSelectorViewModel);
    }

}
