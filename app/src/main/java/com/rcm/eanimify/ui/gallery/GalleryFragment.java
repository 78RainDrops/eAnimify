package com.rcm.eanimify.ui.gallery;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rcm.eanimify.R;
import com.rcm.eanimify.adapters.ImageAdapter;
import com.rcm.eanimify.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;
    private ImageAdapter imageAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        GalleryViewModel galleryViewModel =
//                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//
//        final TextView textView = binding.textGallery;
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        galleryViewModel = new ViewModelProvider(this, new GalleryViewModel.GalleryViewModelFactory(requireActivity().getApplication())).get(GalleryViewModel.class);

        RecyclerView imageRecyclerView = binding.imageRecyclerView;
        imageAdapter = new ImageAdapter(requireContext()); // Initialize ImageAdapter

        galleryViewModel.getImageUrisLiveData().observe(getViewLifecycleOwner(), imageEntities -> {
            SharedPreferences preferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String userId = preferences.getString("userId", null); // Get user ID

            if (imageEntities != null && !imageEntities.isEmpty()) {
                // ... (update adapter and hide empty message) ...
                Toast.makeText(requireContext(), "Images loaded for user: " + userId, Toast.LENGTH_SHORT).show();
            } else {
                // ... (show empty message) ...
                Toast.makeText(requireContext(), "No images found for user: " + userId, Toast.LENGTH_SHORT).show();
            }
            imageAdapter.setImages(imageEntities);
        });

        imageRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        imageRecyclerView.setAdapter(imageAdapter); // Set adapter after data is loaded
    }
}
//package com.rcm.eanimify.ui.gallery;
//
//import androidx.lifecycle.Observer;
//import androidx.lifecycle.ViewModelProvider;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.rcm.eanimify.R;
//import com.rcm.eanimify.data.local.ImageEntity;
//import com.rcm.eanimify.databinding.FragmentGalleryBinding;
//import com.rcm.eanimify.adapters.ImageAdapter;
//import java.util.List;
//
//public class GalleryFragment extends Fragment {
//
//    private FragmentGalleryBinding binding;
//    private GalleryViewModel galleryViewModel;
//    private ImageAdapter imageAdapter;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        galleryViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(GalleryViewModel.class);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
//
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        imageAdapter = new ImageAdapter(getContext()); // Initialize adapter
//        recyclerView.setAdapter(imageAdapter);
//
//        galleryViewModel.getAllImages().observe(getViewLifecycleOwner(), new Observer<List<ImageEntity>>() {
//            @Override
//            public void onChanged(List<ImageEntity> imageEntities) {
//                imageAdapter.setImages(imageEntities);
//            }
//        });
//
//        return view;
//    }


//    private GalleryViewModel galleryViewModel;
//    private ImageAdapter imageAdapter;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
//
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        imageAdapter = new ImageAdapter(getContext()); // Initialize adapter
//        recyclerView.setAdapter(imageAdapter);
//
//        galleryViewModel.getAllImages().observe(getViewLifecycleOwner(), new Observer<List<ImageEntity>>() {
//            @Override
//            public void onChanged(List<ImageEntity> imageEntities) {
//                imageAdapter.setImages(imageEntities);
//            }
//        });
//
//        return view;
//    }

//    private GalleryViewModel galleryViewModel;
//    private ImageAdapter imageAdapter;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
////        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
//
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        imageAdapter = new ImageAdapter(getContext()); // Initialize adapter
//        recyclerView.setAdapter(imageAdapter);
//
//        galleryViewModel.getAllImages().observe(getViewLifecycleOwner(), new Observer<List<ImageEntity>>() {
//            @Override
//            public void onChanged(List<ImageEntity> imageEntities) {
//                imageAdapter.setImages(imageEntities);
//            }
//        });
//
//        return view;
//    }

//    private GalleryViewModel galleryViewModel;
//    ImageAdapter imageAdapter;
//    private View view;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // ... other code ...
//
//        galleryViewModel.getAllImages().observe(getViewLifecycleOwner(), new Observer<List<ImageEntity>>() {
//            @Override
//            public void onChanged(List<ImageEntity> imageEntities) {
//
//                imageAdapter.setImages(imageEntities);
//            }
//        });
//
//        return view;
//    }

//    private FragmentGalleryBinding binding;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        GalleryViewModel galleryViewModel =
//                new ViewModelProvider(this).get(GalleryViewModel.class);
//
//        binding = FragmentGalleryBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textGallery;
////        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;
//    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//}