package com.mrichard.napkin_handler.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
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

import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.image.ImageUtils;
import com.mrichard.napkin_handler.data.image_recognition.ImageRecognizer;
import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.databinding.FragmentHomeBinding;

import java.io.File;
import java.io.IOException;

public class HomeFragment extends Fragment {

    private NapkinDB napkinDB;

    private FragmentHomeBinding binding;

    private HomeViewModel homeViewModel;

    private ImageRecognizer imageRecognizer;

    private ImageUtils imageUtils;

    private File showedPicture;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Antipattern, but works.
        napkinDB = NapkinDB.GetInstance(getContext());

        imageRecognizer = new ImageRecognizer(getContext());

        imageUtils = new ImageUtils();

        // Inflating fragment.
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // We open the camera.
        binding.buttonTakePicture.setOnClickListener(view -> {
            // Checking permission.
            if (
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 110);
            } else {
                try {
                    // We have permission to use the camera.
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // We will save the picture in this UUID file.
                    File imageFile = imageUtils.createImageFile(getContext());
                    Uri photoURI = FileProvider.getUriForFile(this.getContext(),
                            "com.mrichard.napkin_handler.fileprovider",
                            imageFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    // ViewModel needs it to know.
                    homeViewModel.getShowedPictureFile().setValue(imageFile);

                    cameraActivityResultLauncher.launch(cameraIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // We open the gallery.
        binding.buttonLaunchGallery.setOnClickListener(view -> {
            try {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // We will save the picture in this UUID file.
                File imageFile = imageUtils.createImageFile(getContext());
                galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFile);
                // ViewModel needs it to know.
                homeViewModel.getShowedPictureFile().setValue(imageFile);

                galleryActivityResultLauncher.launch(galleryIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Image showing.
        homeViewModel.getThumbnailBitmap().observe(getViewLifecycleOwner(), binding.imageViewPicture::setImageBitmap);

        // Image class name binding.
        homeViewModel.getClassifiedText().observe(getViewLifecycleOwner(), binding.textviewClassified::setText);

        // Image for saving.
        homeViewModel.getShowedPictureFile().observe(getViewLifecycleOwner(), this::onShowedPictureFileChanged);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * The showed picture is changed.
     *
     * @param showedPicture
     */
    protected void onShowedPictureFileChanged(File showedPicture) {
        this.showedPicture = showedPicture;
    }

    /**
     * Starting camera activity to capture picture.
     * Ugly AF.
     */
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String filePath = showedPicture.getAbsolutePath();
                    Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);

                    processImageBitmap(imageBitmap);
                }
            });

    /**
     * Starting gallery activity.
     * We use this abomination as StartActivityForResult is deprecated.
     * It is horrible as its readability is next to null... but this is the way.
     */
    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri dat = data.getData();

                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), dat);

                        processImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    // Processing image bitmap.
    private void processImageBitmap(Bitmap imageBitmap) {
        // We do the computation in a new thread.
        Thread processorThread = new Thread() {

            @Override
            public void run() {
                super.run();

                // Creating the thumbnail image.
                int dimension = Math.min(imageBitmap.getWidth(), imageBitmap.getHeight());
                Bitmap thumbnailImage = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension);

                // We are on a separate thread and we have to do any changes
                // on the GUI on the UI thread.
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show image through the homeViewModel.
                        homeViewModel.getThumbnailBitmap().setValue(thumbnailImage);

                        // We don't know the classified name for now.
                        homeViewModel.getClassifiedText().setValue("");
                    }
                });

                // Doing the actual computation of the image processing.
                String classifiedName = imageRecognizer.recognize(imageBitmap);

                // We have to do it on the UI thread again.
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        homeViewModel.getClassifiedText().setValue(classifiedName);
                    }
                });
            }

        };

        // Starting the thread.
        processorThread.start();
    }

}