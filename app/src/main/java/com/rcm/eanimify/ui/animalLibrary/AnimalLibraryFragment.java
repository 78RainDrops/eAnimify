package com.rcm.eanimify.ui.animalLibrary;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rcm.eanimify.MainActivity;
import com.rcm.eanimify.MyApplication;
import com.rcm.eanimify.R;
import com.rcm.eanimify.adapters.AnimalLibraryAdapter;
import com.rcm.eanimify.databinding.FragmentAnimalLibraryBinding;

import java.util.ArrayList;

public class AnimalLibraryFragment extends Fragment  {

    private FragmentAnimalLibraryBinding binding;
    private RecyclerView recyclerView;
    private AnimalLibraryAdapter adapter;
    private AnimalLibraryViewModel animalLibraryViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_animal_library, container, false);
        binding = FragmentAnimalLibraryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ProgressBar progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        adapter = new AnimalLibraryAdapter(new ArrayList<>()); // Create an empty adapter
        adapter = new AnimalLibraryAdapter(new ArrayList<>(), requireContext(), getChildFragmentManager());
        recyclerView.setAdapter(adapter);

        animalLibraryViewModel = new ViewModelProvider(this).get(AnimalLibraryViewModel.class);


//        final TextView textView = binding.textLibrary;
//        animalLibraryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        AnimalLibraryViewModel sharedViewModel = myApplication.getSharedViewModel();

        sharedViewModel.data.observe(getViewLifecycleOwner(), commonNames -> {
            if (commonNames != null) {
                // Update the existing adapter's data
                adapter.setDataList(commonNames);
                adapter.notifyDataSetChanged(); // Notify adapter of data change
                progressBar.setVisibility(View.GONE);
            }
        });
        return root;

    }


//    @Override
//    public void onAnimalClick(String animalName) {
//        NavController navController = Navigation.findNavController(requireView());
//        navController.navigate(R.id.action_animalLibraryFragment_to_animalDetailsFragment);
//    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}