package com.mrichard.napkin_handler.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mrichard.napkin_handler.data.model.category.Category;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<Category>> categories;

    public NotificationsViewModel() {
        categories = new MutableLiveData<>();
    }

    public void setCategories(List<Category> categories) {
        this.categories.setValue(categories);
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

}
