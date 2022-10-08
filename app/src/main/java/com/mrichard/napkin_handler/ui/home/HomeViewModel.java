package com.mrichard.napkin_handler.ui.home;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> classifiedText;

    private final MutableLiveData<Bitmap> imageBitmap;

    public HomeViewModel() {
        classifiedText = new MutableLiveData<>();
        classifiedText.setValue("-");

        imageBitmap = new MutableLiveData<>();
    }

    public MutableLiveData<String> getClassifiedText() {
        return classifiedText;
    }

    public MutableLiveData<Bitmap> getImageBitmap() {
        return imageBitmap;
    }
}