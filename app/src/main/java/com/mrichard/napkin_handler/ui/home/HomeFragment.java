package com.mrichard.napkin_handler.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.mrichard.napkin_handler.data.image_recognition.ImageRecognizerStore;
import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.databinding.FragmentHomeBinding;

import java.io.File;
import java.io.IOException;

public class HomeFragment extends Fragment {

    private NapkinDB napkinDB;

    private FragmentHomeBinding binding;

    private HomeViewModel homeViewModel;

    private ImageUtils imageUtils;

    private File imageFile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Antipattern, but works.
        napkinDB = NapkinDB.GetInstance(getContext());
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
                    imageFile = imageUtils.createImageFile(getContext());
                    Uri photoURI = FileProvider.getUriForFile(this.getContext(),
                            "com.mrichard.napkin_handler.fileprovider",
                            imageFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    // ViewModel needs it to know.
                    homeViewModel.setShowedPictureFile(imageFile);

                    cameraActivityResultLauncher.launch(cameraIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // We open the gallery.
        binding.buttonLaunchGallery.setOnClickListener(view -> {
            if (
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 110);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // We can pick multiple pictures.
                galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                galleryActivityResultLauncher.launch(galleryIntent);
            }
        });

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

    /**
     * Starting gallery activity.
     * We use this abomination as StartActivityForResult is deprecated.
     * It is horrible as its readability is next to null... but this is the way.
     */
    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData().getClipData() != null) {
                            int pictureCount = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < pictureCount; i++) {
                                Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                File imageFile = new File(imageUtils.saveFile(getContext(), imageUri));

                                processImageBitmap(imageFile);
                            }
                        }
                }
            });

    // Processing image bitmap.
    private void processImageBitmap(File imageFile) {
        // We do the computation in a new thread.
        Thread processorThread = new Thread() {

            @Override
            public void run() {
                super.run();

                // Decoding image bitmap.
                // It is synchronized as database operations are not threadsafe.
                Picture picture = Picture.FromFile(imageFile, ImageRecognizerStore.GetInstance(getContext()));

                // By default we have 1 picture.
                // The logic behind this is that if we took a picture by camera of a napkin
                // then we have at least one of it.
                // Of course this does not apply for the gallery import, but don't see any reason
                // we not to suppose that we have at least one napkin from each of the
                // picked pictures.
                picture.setCount(1);

                synchronized (napkinDB) {
                    napkinDB.pictureDao().insert(
                            picture
                    );
                }
            }

        };

        // Starting the thread.
        processorThread.start();
    }

}