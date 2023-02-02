package com.mrichard.napkin_handler.ui.dashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.image.ImageUtils;
import com.mrichard.napkin_handler.data.image_recognition.ImageRecognizerStore;
import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.databinding.FragmentDashboardBinding;
import com.mrichard.napkin_handler.ui.dashboard.adapter.PictureGalleryAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private PictureGalleryAdapter pictureGalleryAdapter;

    private DashboardViewModel dashboardViewModel;

    private ImageUtils imageUtils;

    private boolean toSwap = false;
    private boolean sorted = false;
    private List<Picture> pictures = new ArrayList<>();
    private List<Picture> showPictures;
    private Set<Long> selectedPictures;

    private File imageFile;
    private Picture processedPicture;

    protected NapkinDB napkinDB = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        napkinDB = NapkinDB.GetInstance(getContext());
        dashboardViewModel.setNapkinDB(napkinDB);
        imageUtils = new ImageUtils();

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pictureGalleryAdapter = new PictureGalleryAdapter(getContext(), getActivity(), dashboardViewModel);
        binding.pictureGallery.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        binding.pictureGallery.setAdapter(pictureGalleryAdapter);

        binding.switchToSwap.setOnCheckedChangeListener((compoundButton, b) -> {
            toSwap = b;

            showPictures();
        });

        binding.buttonSharePictures.setOnClickListener(view -> {
            sharePictures(selectedPictures);
        });

        binding.buttonSearchByCamera.setOnClickListener(view -> {
            // Checking permission.
            if (
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA }, 110);
            } else {
                try {
                    // We have permission to use the camera.
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // We will save the picture in this UUID file.
                    imageFile = imageUtils.createImageFile(getContext());
                    Uri photoURI = FileProvider.getUriForFile(this.getContext(),
                            "com.mrichard.napkin_handler.fileprovider",
                            imageFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    cameraActivityResultLauncher.launch(cameraIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.buttonSimilarPictures.setOnClickListener(view -> {
            sorted = true;

            // When sort the items by their similarity then we have to show the most
            // similar picture.
            // The most similar picture is the selected picture itself but if we show that
            // picture then we can see the next one which is the second most similar
            // to it.
            binding.pictureGallery.scrollToPosition(0);

            showPictures();
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

    /**
     * Starting camera activity to capture picture.
     * Ugly AF.
     */
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    processImageBitmap(imageFile);
                }
            });

    private void onPicturesChanged(List<Picture> pictures) {
        this.pictures = pictures;

        showPictures();
    }

    protected void processImageBitmap(File imageFile) {
        processedPicture = Picture.FromFile(imageFile, ImageRecognizerStore.GetInstance(getContext()));

        showPictures();
    }

    private void showPictures() {
        Thread thread = new Thread(() -> {
            if (sorted) {
                if (!selectedPictures.isEmpty()) {
                    Picture selectedPicture = napkinDB.pictureDao().getPicture(selectedPictures.iterator().next());
                    if (selectedPicture != null) {
                        for (Picture picture : pictures) {
                            picture.setSimilarityForPicture(selectedPicture);
                        }
                    }
                    Collections.sort(pictures);

                    // The picture is no longer selected.
                    getActivity().runOnUiThread(() -> dashboardViewModel.onClickPicture(selectedPicture.getId()));
                }
            }
            {
                if (processedPicture != null) {
                    for (Picture picture : pictures) {
                        picture.setSimilarityForPicture(processedPicture);
                    }

                    Collections.sort(pictures);

                    // Deleting the file.
                    processedPicture = null;
                    imageFile.delete();

                    // Showing the pictures from the first pictures.
                    getActivity().runOnUiThread(() -> binding.pictureGallery.scrollToPosition(0));
                }
            }

            showPictures = pictures.stream()
                    .filter(picture -> !toSwap || picture.getCount() >= 2)
                    .collect(Collectors.toList());

            getActivity().runOnUiThread(() -> pictureGalleryAdapter.setPictures(showPictures));
        });
        thread.start();
    }

    private void onSelectedPicturesChanged(Set<Long> selectedPictures) {
        this.selectedPictures = selectedPictures;

        pictureGalleryAdapter.setSelectedPictures(selectedPictures);

        binding.buttonSharePictures.setEnabled(selectedPictures.size() != 0);

        binding.buttonSimilarPictures.setEnabled(selectedPictures.size() == 1);
        if (!binding.buttonSimilarPictures.isEnabled()) {
            sorted = false;
        }
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
