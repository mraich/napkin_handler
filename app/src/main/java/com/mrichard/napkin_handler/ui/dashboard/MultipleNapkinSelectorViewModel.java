package com.mrichard.napkin_handler.ui.dashboard;

import com.mrichard.napkin_handler.data.viewmodel.NapkinSelectorViewModel;

/**
 * This is a Napkin Selector ViewModel.
 * With this ViewModel we can select multiple napkins at the same time.
 */
public class MultipleNapkinSelectorViewModel extends NapkinSelectorViewModel {

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