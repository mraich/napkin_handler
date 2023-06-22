package com.mrichard.napkin_handler.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mrichard.napkin_handler.R;
import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.model.category.Category;
import com.mrichard.napkin_handler.data.model.picture.Picture;
import com.mrichard.napkin_handler.data.viewmodel.SelectorViewModelBase;
import com.mrichard.napkin_handler.databinding.CategoryItemBinding;
import com.mrichard.napkin_handler.ui.FontManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private Context context;

    private Activity activity;

    private SelectorViewModelBase categorySelectorViewModel;

    private List<Category> categories;

    private Set<Long> selectedCategories;

    public CategoryAdapter(Context context, Activity activity) {
        this(context, activity, null);
    }

    public CategoryAdapter(Context context, Activity activity, SelectorViewModelBase categorySelectorViewModel) {
        this.context = context;

        this.activity = activity;

        this.categorySelectorViewModel = categorySelectorViewModel;

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

    public void setSelectedCategories(Set<Long> selectedCategories) {
        this.selectedCategories = selectedCategories;

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, int position) {
        final Category category = categories.get(position);

        showSelection(holder, category);

        holder.getBinding().categoryNameTextView.setText(category.getName());
        // Clicking the picture.
        holder.getBinding().getRoot().setOnClickListener(view -> {
            if (categorySelectorViewModel != null) {
                categorySelectorViewModel.onClickPicture(category.getId());

                showSelection(holder, category);
            }
        });
        holder.getBinding().getRoot().setOnLongClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

            alert.setTitle(view.getResources().getString(R.string.are_you_sure_to_delete_category));

            final TextView categoryTextView = new TextView(view.getContext());
            alert.setView(categoryTextView);

            categoryTextView.setText(category.getName());

            alert.setPositiveButton(view.getResources().getString(R.string.ok), (dialog, whichButton) -> {
                Thread thread = new Thread(() -> {
                    NapkinDB napkinDB = NapkinDB.GetInstance(view.getContext());
                    napkinDB.categoryDao().delete(category);
                });
                thread.start();
            });

            alert.setNegativeButton(view.getResources().getString(R.string.cancel), (dialog, whichButton) -> { });

            alert.show();

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // Showing the selection of a category.
    private void showSelection(CategoryViewHolder holder, Category category) {
        if (selectedCategories != null && selectedCategories.contains(category.getId())) {
            holder.getBinding().getRoot().setBackgroundColor(context.getResources().getColor(com.google.android.material.R.color.material_blue_grey_800));
        } else {
            holder.getBinding().getRoot().setBackgroundColor(context.getResources().getColor(com.google.android.material.R.color.m3_ref_palette_white));
        }
    }

}
