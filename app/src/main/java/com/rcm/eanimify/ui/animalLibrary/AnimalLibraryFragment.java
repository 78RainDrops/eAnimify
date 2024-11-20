package com.rcm.eanimify.ui.animalLibrary;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.firestore.FirebaseFirestore;
import com.rcm.eanimify.MyApplication;
import com.rcm.eanimify.R;
import com.rcm.eanimify.adapters.AnimalLibraryAdapter;
import com.rcm.eanimify.databinding.FragmentAnimalLibraryBinding;


import java.util.ArrayList;

public class AnimalLibraryFragment extends Fragment  implements SharedPreferences.OnSharedPreferenceChangeListener {

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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

//        final TextView textView = binding.textLibrary;
//        animalLibraryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        AnimalLibraryViewModel sharedViewModel = myApplication.getSharedViewModel();
//
//        if(isNetworkAvailable()){
//            sharedViewModel.fetchDataFromFirebase();
//        }else {
//            sharedViewModel.loadDataFromPreferences();
//        }

        sharedViewModel.fetchDataFromFirebase();

        sharedViewModel.data.observe(getViewLifecycleOwner(), commonNames -> {
            if (commonNames != null) {
                // Update the existing adapter's data
                adapter.setDataList(commonNames);
                adapter.notifyDataSetChanged(); // Notify adapter of data change
                progressBar.setVisibility(View.GONE);
            }
        });

        SearchView searchView = binding.searchBar.searchView;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission (optional)
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle search query text changes
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        Spinner sortingSpinner = root.findViewById(R.id.sortingSpinner); // Assuming 'sortingSpinner' is the ID of your Spinner

// Create an ArrayAdapter for the dropdown options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sorting_options, // Create an array resource 'sorting_options' with "Alphabetical" and "Endanger Level"
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortingSpinner.setAdapter(adapter);

// Set a listener for the Spinner
        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Name")) {
                    sharedViewModel.setSortingOption("alphabetical");
                } else if (selectedOption.equals("Endanger Level")) {
                    sharedViewModel.setSortingOption("endangerLevel");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        return root;

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =  (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("font_size")) {
            // Font size preference changed, update adapter
            if (adapter != null) {
                adapter.notifyDataSetChanged(); // Or use a more targeted update method
            }
        }
    }
//    @Override
//    public void onAnimalClick(String animalName) {
//        NavController navController = Navigation.findNavController(requireView());
//        navController.navigate(R.id.action_animalLibraryFragment_to_animalDetailsFragment);
//    }
    @Override
    public void onDestroyView() {
    super.onDestroyView();
    // Unregister listener to avoid memory leaks
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this);
        binding = null;
    }

}