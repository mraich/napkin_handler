package com.mrichard.napkin_handler.ui.dashboard;

import com.mrichard.napkin_handler.data.viewmodel.SelectorViewModelBase;

/**
 * This is a Napkin Selector ViewModel.
 * With this ViewModel we can select multiple napkins at the same time.
 */
public class MultipleNapkinSelectorViewModel extends SelectorViewModelBase {

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