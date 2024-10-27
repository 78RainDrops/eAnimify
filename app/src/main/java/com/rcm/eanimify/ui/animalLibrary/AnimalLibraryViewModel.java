package com.rcm.eanimify.ui.animalLibrary;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class AnimalLibraryViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<String> mText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<ArrayList<String>> _data = new MutableLiveData<ArrayList<String>>();
    public LiveData<ArrayList<String>> data = _data;

    public AnimalLibraryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is aniMAL Library fragment");
//        fetchDataFromFirebase();
    }

    public void fetchDataFromFirebase() {
        db.collection("Endemic Animals")
                .get()
                .addOnSuccessListener(result -> {
                    ArrayList<String> commonNames = new ArrayList<>();
                    for (QueryDocumentSnapshot document : result) {
                        String commonName = document.getString("Common Name");
                        if (commonName != null) {
                            commonNames.add(commonName);
                        }
                    }
                    Collections.sort(commonNames);
                    _data.setValue(commonNames); // Store common names in LiveData
                })
                .addOnFailureListener(exception -> {
                    // Handle error
                    Log.e("AnimalLibraryViewModel", "Error fetching data: ", exception);
                });
    }


    public LiveData<String> getText() {
        return mText;
    }

}