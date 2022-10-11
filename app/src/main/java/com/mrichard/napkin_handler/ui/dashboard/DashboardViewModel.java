package com.mrichard.napkin_handler.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.model.picture.Picture;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardViewModel extends ViewModel {

    private NapkinDB napkinDB;

    // The ids of the selected pictures.
    private MutableLiveData<Set<Long>> selectedPictures = new MutableLiveData<>();

    public DashboardViewModel() {
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

    public void onClickPicture(Long id) {
        if (!selectedPictures.getValue().contains(id)) {
            selectedPictures.getValue().add(id);
        } else {
            selectedPictures.getValue().remove(id);
        }

        // Refreshing. :)
        selectedPictures.setValue(selectedPictures.getValue());
    }

}