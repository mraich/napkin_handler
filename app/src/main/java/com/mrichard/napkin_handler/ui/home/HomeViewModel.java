package com.mrichard.napkin_handler.ui.home;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> classifiedText;

    private final MutableLiveData<Bitmap> thumbnailBitmap;

    private final MutableLiveData<File> showedPictureFile;

    public HomeViewModel() {
        classifiedText = new MutableLiveData<>();
        classifiedText.setValue("-");

        thumbnailBitmap = new MutableLiveData<>();

        showedPictureFile = new MutableLiveData<>();
    }

    public MutableLiveData<String> getClassifiedText() {
        return classifiedText;
    }

    public MutableLiveData<Bitmap> getThumbnailBitmap() {
        return thumbnailBitmap;
    }

    public MutableLiveData<File> getShowedPictureFile() { return showedPictureFile; }

}