package com.rcm.eanimify.ui.animalLibrary;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rcm.eanimify.R;
import com.rcm.eanimify.databinding.FragmentAnimalLibraryBinding;
public class AnimalLibraryFragment extends Fragment {

    private FragmentAnimalLibraryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AnimalLibraryViewModel galleryViewModel =
                new ViewModelProvider(this).get(AnimalLibraryViewModel.class);

        binding = FragmentAnimalLibraryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLibrary;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}