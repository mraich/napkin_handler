package com.mrichard.napkin_handler.ui.dashboard;

import com.mrichard.napkin_handler.data.viewmodel.SelectorViewModelBase;

/**
 * This is a Category Selector ViewModel.
 * With this ViewModel we can select multiple categories at the same time.
 */
public class MultipleCategorySelectorViewModel extends SelectorViewModelBase {

    public void onClickPicture(Long id) {
        if (!selected.getValue().contains(id)) {
            selected.getValue().add(id);
        } else {
            selected.getValue().remove(id);
        }

        // Refreshing. :)
        selected.setValue(selected.getValue());
    }

}