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

}
