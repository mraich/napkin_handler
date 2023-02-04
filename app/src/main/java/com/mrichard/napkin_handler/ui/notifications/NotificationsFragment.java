package com.mrichard.napkin_handler.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mrichard.napkin_handler.data.db.NapkinDB;
import com.mrichard.napkin_handler.data.model.category.Category;
import com.mrichard.napkin_handler.databinding.FragmentNotificationsBinding;
import com.mrichard.napkin_handler.ui.adapter.CategoryAdapter;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private NapkinDB napkinDB;

    private CategoryAdapter categoryAdapter;

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        // Antipattern, but works.
        napkinDB = NapkinDB.GetInstance(getContext());

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        categoryAdapter = new CategoryAdapter(getContext(), getActivity());
        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        binding.recyclerViewCategories.setAdapter(categoryAdapter);

        napkinDB.categoryDao().getAll().observe(getViewLifecycleOwner(), this::onCategoriesChanged);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Showing new categories.
     *
     * @param newCategories
     */
    private void onCategoriesChanged(List<Category> newCategories) {
        getActivity().runOnUiThread(() -> categoryAdapter.setCategories(newCategories));
    }

}
