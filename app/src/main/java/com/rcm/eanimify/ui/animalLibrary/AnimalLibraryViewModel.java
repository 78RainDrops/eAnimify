package com.rcm.eanimify.ui.animalLibrary;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnimalLibraryViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<ArrayList<String>> _data = new MutableLiveData<ArrayList<String>>();
    public LiveData<ArrayList<String>> data = _data;
    private String currentSortingOption = "alphabetical";

    public AnimalLibraryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is aniMAL Library fragment");
//        fetchDataFromFirebase();
    }



    public void fetchDataFromFirebase() {
        db.collection("Endemic Animals")
                .get()
                .addOnSuccessListener(result -> {
                    ArrayList<String> displayList = new ArrayList<>();

                    if (currentSortingOption.equals("alphabetical")) {
                        // Alphabetical sorting (all animals)
                        for (QueryDocumentSnapshot document : result) {
                            String commonName = document.getString("Common Name");
                            if (commonName != null) {
                                displayList.add(commonName);
                            }
                        }
                        Collections.sort(displayList); // Sort all animals alphabetically
                    } else if (currentSortingOption.equals("endangerLevel")) {
                        // Group by Endanger Level and sort within groups
                        LinkedHashMap<String, ArrayList<String>> groupedAnimals = new LinkedHashMap<>();
                        for (QueryDocumentSnapshot document : result) {
                            String commonName = document.getString("Common Name");
                            String endangerLevel = document.getString("Endanger Level");

                            if (commonName != null && endangerLevel != null) {
                                if (!groupedAnimals.containsKey(endangerLevel)) {
                                    groupedAnimals.put(endangerLevel, new ArrayList<>());
                                }
                                groupedAnimals.get(endangerLevel).add(commonName);
                            }
                        }

                        // Sort Endanger Levels and animals within groups
                        List<String> endangerLevels = new ArrayList<>(groupedAnimals.keySet());

                        Collections.sort(endangerLevels, (a, b) -> {

                            if (a.equals("Critically Endangered (CR)")) return -1;
                            if (b.equals("Critically Endangered (CR)")) return 1;
                            if (a.equals("Endangered (EN)")) return -1;
                            if (b.equals("Endangered (EN)")) return 1;
                            if (a.equals("Vulnerable (YU)")) return -1;
                            if (b.equals("Vulnerable (YU)")) return 1;
                            if (a.equals("Other Threatened Species (OTS)")) return -1;
                            if (b.equals("Other Threatened Species (OTS)")) return 1;
                            // ... and so on for other Endanger Levels
                            return a.compareTo(b); // Default alphabetical sorting if no specific order is defined
                        });
                        for (String endangerLevel : endangerLevels) {
                            ArrayList<String> animalsInGroup = groupedAnimals.get(endangerLevel);
                            Collections.sort(animalsInGroup); // Sort animals within the group alphabetically
                            displayList.add(endangerLevel); // Add Endanger Level header
                            displayList.addAll(animalsInGroup); // Add animals in the group
                        }
                    }

                    _data.setValue(displayList);
                })
                .addOnFailureListener(exception -> {
                    // Handle error
                    Log.e("AnimalLibraryViewModel", "Error fetching data: ", exception);
                });
    }



    // Implement getEndangerLevel() to fetch the Endanger Level for an animal
    private String getEndangerLevel(String animalName) {
        final String[] endangerLevel = {""}; // To store the Endanger Level

        db.collection("Endemic Animals")
                .whereEqualTo("Common Name", animalName) // Query for the animal by its Common Name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first document (assuming there's only one document per animal)
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                        endangerLevel[0] = document.getString("Endanger Level"); // Get the "Endanger Level" field
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e("AnimalLibraryViewModel", "Error fetching Endanger Level: ", e);
                });

        return endangerLevel[0]; // Return the Endanger Level
    }

    // Add a method to update the sorting option
    public void setSortingOption(String sortingOption) {
        currentSortingOption = sortingOption;
        fetchDataFromFirebase(); // Re-fetch data with the new sorting option
    }
    public LiveData<String> getText() {
        return mText;
    }

}