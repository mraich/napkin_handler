package com.mrichard.napkin_handler.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.mrichard.napkin_handler.databinding.CategoryItemBinding;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private CategoryItemBinding binding;

    public CategoryViewHolder(CategoryItemBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public CategoryItemBinding getBinding() {
        return binding;
    }

}
