package com.rcm.eanimify;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.firebase.FirebaseApp;
import com.rcm.eanimify.ui.animalLibrary.AnimalLibraryViewModel;

public class MyApplication extends Application implements ViewModelStoreOwner {

    private AnimalLibraryViewModel sharedViewModel;
    private ViewModelStore appViewModelStore;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Create shared ViewModel
        sharedViewModel = new ViewModelProvider( this).get(AnimalLibraryViewModel.class);
        sharedViewModel.fetchDataFromFirebase();
    }

    public AnimalLibraryViewModel getSharedViewModel() {
        return sharedViewModel;
    }

    @Override
    public ViewModelStore getViewModelStore() {
        if (appViewModelStore == null) {
            appViewModelStore = new ViewModelStore();
        }
        return appViewModelStore;
    }
}
