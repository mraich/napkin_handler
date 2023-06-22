package com.mrichard.napkin_handler.data.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Set;

/**
 * This is the base ViewModel for the ViewModels used to
 * handle the selection of the napkins.
 */
public abstract class SelectorViewModelBase extends ViewModel {

    // The ids of the selected data.
    protected MutableLiveData<Set<Long>> selected = new MutableLiveData<>();

    public SelectorViewModelBase() {
        setSelected(new HashSet<>());
    }

    public LiveData<Set<Long>> selected() {
        return selected;
    }

    public void setSelected(Set<Long> selected_) { selected.setValue(selected_); }

    public void clearSelection() { selected.setValue(new HashSet<>()); }

    public abstract void onClickPicture(Long id);

}
