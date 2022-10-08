package com.mrichard.napkin_handler.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mrichard.napkin_handler.data.image_recognition.ImageRecognizer;
import com.mrichard.napkin_handler.databinding.FragmentHomeBinding;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private HomeViewModel homeViewModel;

    private ImageRecognizer imageRecognizer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        imageRecognizer = new ImageRecognizer(getContext());

        // Inflating fragment.
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // We open the gallery.
        binding.buttonLaunchGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryActivityResultLauncher.launch(galleryIntent);
            }
        });

        // Image showing.
        homeViewModel.getThumbnailBitmap().observe(getViewLifecycleOwner(), binding.imageViewPicture::setImageBitmap);

        // Image class name binding.
        homeViewModel.getClassifiedText().observe(getViewLifecycleOwner(), binding.textviewClassified::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

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

                        // Creating thumbnail.
                        int dimension = Math.min(imageBitmap.getWidth(), imageBitmap.getHeight());
                        Bitmap thumbnailImage = ThumbnailUtils.extractThumbnail(imageBitmap, dimension, dimension);

                        // Show image through the homeViewModel.
                        homeViewModel.getThumbnailBitmap().setValue(thumbnailImage);

                        String classifiedName = imageRecognizer.recognize(imageBitmap);
                        homeViewModel.getClassifiedText().setValue(classifiedName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
}