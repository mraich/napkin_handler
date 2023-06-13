package com.mrichard.napkin_handler.data.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.model.picture.Picture;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is the base ViewModel for the ViewModels used to
 * handle the selection of the napkins.
 */
public abstract class NapkinSelectorViewModel extends ViewModel {

    protected NapkinDB napkinDB;

    // The ids of the selected pictures.
    protected MutableLiveData<Set<Long>> selectedPictures = new MutableLiveData<>();

    public NapkinSelectorViewModel() {
        selectedPictures.setValue(new HashSet<>());
    }

    public void setNapkinDB(NapkinDB napkinDB) {
        this.napkinDB = napkinDB;
    }

    public LiveData<List<Picture>> getPictures() {
        return napkinDB.pictureDao().getAll();
    }

    public LiveData<Set<Long>> selectedPictures() {
        return selectedPictures;
    }

    public abstract void onClickPicture(Long id);

}
