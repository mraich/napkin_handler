package com.mrichard.napkin_handler.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.databinding.FragmentDashboardBinding;
import com.mrichard.napkin_handler.ui.dashboard.adapter.PictureGalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private PictureGalleryAdapter pictureGalleryAdapter;

    private Set<Long> selectedPictures;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        dashboardViewModel.setNapkinDB(NapkinDB.GetInstance(getContext()));

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pictureGalleryAdapter = new PictureGalleryAdapter(getContext(), dashboardViewModel);
        binding.pictureGallery.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        binding.pictureGallery.setAdapter(pictureGalleryAdapter);

        binding.buttonSharePictures.setOnClickListener(view -> {
            sharePictures(selectedPictures);
        });

        dashboardViewModel.getPictures().observe(getViewLifecycleOwner(), this::onPicturesChanged);

        dashboardViewModel.selectedPictures().observe(getViewLifecycleOwner(), this::onSelectedPicturesChanged);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onPicturesChanged(List<Picture> pictures) {
        pictureGalleryAdapter.setPictures(pictures);
    }

    private void onSelectedPicturesChanged(Set<Long> selectedPictures) {
        this.selectedPictures = selectedPictures;

        pictureGalleryAdapter.setSelectedPictures(selectedPictures);

        binding.buttonSimilarPictures.setEnabled(selectedPictures.size() == 1);
    }

    /**
     * Sharing the pictures.
     *
     * @param pictures
     */
    private void sharePictures(Set<Long> pictures) {
        Thread thread = new Thread(() -> {
            NapkinDB napkinDB = NapkinDB.GetInstance(getContext());

            ArrayList<Uri> pictureUris = new ArrayList<>();
            for (Long pictureId: pictures) {
                pictureUris.add(FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", new File(napkinDB.pictureDao().getPicture(pictureId).getPath())));
            }

            Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, pictureUris);

            startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
        });
        thread.start();
    }

}
