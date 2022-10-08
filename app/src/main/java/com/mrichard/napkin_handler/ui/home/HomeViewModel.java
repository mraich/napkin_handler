package com.mrichard.napkin_handler.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> classifiedText;

    public HomeViewModel() {
        classifiedText = new MutableLiveData<>();
        classifiedText.setValue("-");
    }

    public LiveData<String> getClassifiedText() {
        return classifiedText;
    }
}