package com.rcm.eanimify.ui.gallery;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rcm.eanimify.R;
import com.rcm.eanimify.adapters.ImageAdapter;
import com.rcm.eanimify.data.local.ImageEntity;
import com.rcm.eanimify.databinding.FragmentGalleryBinding;
import com.rcm.eanimify.decoration.ImageItemDecoration;

import java.util.List;

public class GalleryFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private ImageAdapter imageAdapter;
    private Button delButton;
    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel = new ViewModelProvider(this, new GalleryViewModel.GalleryViewModelFactory(requireActivity().getApplication())).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setFontSize(sharedPreferences);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this); // Unregister the listener
        binding = null;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//
        RecyclerView imageRecyclerView = binding.imageRecyclerView;
        imageAdapter = new ImageAdapter(requireContext()); // Initialize ImageAdapter

        galleryViewModel.getImageUrisLiveData().observe(getViewLifecycleOwner(), imageEntities -> {
            if (imageEntities.isEmpty()) {
                binding.emptyTextView.setVisibility(View.VISIBLE);  // Show "No images to display"
                binding.imageRecyclerView.setVisibility(View.GONE); // Hide RecyclerView
            } else {
                binding.emptyTextView.setVisibility(View.GONE);  // Hide "No images to display"
                binding.imageRecyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
            }
            imageAdapter.setImages(imageEntities); // Update the adapter with new data
        });

        imageRecyclerView.addItemDecoration(new ImageItemDecoration(getResources().getDimensionPixelSize(R.dimen.image_margin)));
        imageRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        imageRecyclerView.setAdapter(imageAdapter); // Set adapter after data is loaded

        delButton = view.findViewById(R.id.deleteButton);
        delButton.setVisibility(View.GONE);
        delButton.setOnClickListener(v -> {
            List<ImageEntity> selectedImages = imageAdapter.getSelectedImages();
            if (!selectedImages.isEmpty()) {
                for (ImageEntity imageEntity : selectedImages) {
                    galleryViewModel.deleteImage(imageEntity.getImageUri());
                }

                // Clear selection and refresh the UI
                imageAdapter.clearSelection();
                imageAdapter.setImages(galleryViewModel.getImageUrisLiveData().getValue()); // Update the adapter
                Toast.makeText(requireContext(), "Images deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No images selected", Toast.LENGTH_SHORT).show();
            }
        });

        imageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                delButton.setVisibility(imageAdapter.isSelectionMode() ? View.VISIBLE : View.GONE);
            }
        });
    }
    private void setFontSize(SharedPreferences sharedPreferences) {
        int fontSize = sharedPreferences.getInt("font_size", 16); // Default to 16sp
        binding.emptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setFontSize(sharedPreferences);
    }
}
