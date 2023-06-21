package com.mrichard.napkin_handler.ui.adapter;

import android.app.Activity;
import android.content.Context;

import com.mrichard.napkin_handler.data.viewmodel.SelectorViewModelBase;

public class PictureGalleryAdapter extends PictureGalleryAdapterBase {

    public PictureGalleryAdapter(Context context, Activity activity, SelectorViewModelBase napkinSelectorViewModel) {
        super(context, activity, napkinSelectorViewModel);
    }

}
