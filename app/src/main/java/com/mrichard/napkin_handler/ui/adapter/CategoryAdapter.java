package com.mrichard.napkin_handler.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mrichard.napkin_handler.data.model.category.Category;
import com.mrichard.napkin_handler.databinding.CategoryItemBinding;
import com.mrichard.napkin_handler.ui.FontManager;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private Context context;

    private Activity activity;

    private List<Category> categories;

    public CategoryAdapter(Context context, Activity activity) {
        this.context = context;

        this.activity = activity;

        this.categories = new ArrayList<>();
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            CategoryItemBinding binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            FontManager.MarkAsIconContainer(binding.getRoot(), FontManager.FONT_AWESOME);

            return new CategoryViewHolder(binding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCategories(List<Category> categories)
    {
        this.categories = categories;

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        final Category category = categories.get(position);

        holder.getBinding().categoryNameTextView.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

}
