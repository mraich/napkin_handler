package com.mrichard.napkin_handler.ui.home;

import com.mrichard.napkin_handler.data.viewmodel.NapkinSelectorViewModel;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a Napkin Selector ViewModel.
 * With this ViewModel we can select single napkins at the same time.
 */
public class SingleNapkinSelectorViewModel extends NapkinSelectorViewModel {

    public void onClickPicture(Long id) {
        Set<Long> selectedPicturesSet = new HashSet<>();
        if (!selected.getValue().contains(id)) {
            selectedPicturesSet.add(id);
        }

        selected.setValue(selectedPicturesSet);

        // Refreshing. :)
        selected.setValue(selected.getValue());
    }

}
