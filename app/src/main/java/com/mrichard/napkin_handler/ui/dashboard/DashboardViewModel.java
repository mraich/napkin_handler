package com.mrichard.napkin_handler.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.model.picture.Picture;

import java.util.List;

public class DashboardViewModel extends ViewModel {

    private NapkinDB napkinDB;

    public DashboardViewModel() {
    }

    public void setNapkinDB(NapkinDB napkinDB) {
        this.napkinDB = napkinDB;
    }

    public LiveData<List<Picture>> getPictures() {
        return napkinDB.pictureDao().getAll();
    }

}