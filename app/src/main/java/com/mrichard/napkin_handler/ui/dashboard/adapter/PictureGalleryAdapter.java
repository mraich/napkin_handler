package com.mrichard.napkin_handler.ui.dashboard.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.databinding.PictureGalleryItemBinding;

import java.util.ArrayList;
import java.util.List;

public class PictureGalleryAdapter extends RecyclerView.Adapter<PictureGalleryViewHolder> {

    private Context mContext;

    private PictureGalleryItemBinding binding;

    private List<Picture> pictures;

    public PictureGalleryAdapter(Context context) {
        this.mContext = context;

        this.pictures = new ArrayList<>();
    }

    @Override
    public PictureGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            binding = PictureGalleryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new PictureGalleryViewHolder(binding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void addPicture(Picture picture) {
        pictures.add(picture);
    }

    public void setPictures(List<Picture> pictures)
    {
        this.pictures = pictures;
    }

    @Override
    public void onBindViewHolder(final PictureGalleryViewHolder holder, int position) {
        final Picture picture = pictures.get(position);

        binding.imageViewPicture.setImageBitmap(BitmapFactory.decodeFile(picture.getPath()));
        binding.imageViewClassified.setText(picture.getAttributes());
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

}
