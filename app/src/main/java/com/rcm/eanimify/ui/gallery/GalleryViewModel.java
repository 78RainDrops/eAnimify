package com.rcm.eanimify.ui.gallery;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rcm.eanimify.data.local.ImageEntity;
import com.rcm.eanimify.data.local.ImageRepository;

import java.util.List;

public class GalleryViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final ImageRepository imageRepository;
    private final MutableLiveData<String> mText;
    private LiveData<List<ImageEntity>> imageUrisLiveData;
    private final Application application;

    public GalleryViewModel(Application application) {
        super();
        this.application = application;
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");

        imageRepository = new ImageRepository(application); // Initialize imageRepository
        SharedPreferences preferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userId = preferences.getString("userId", null);

//        LiveData<List<ImageEntity>> allImages = imageRepository.getImagesForUser(userId);

        imageUrisLiveData = imageRepository.getImagesForUser(userId);

    }

    public LiveData<String> getText() {
        return mText;
    }
    // Add this method to expose imageUrisLiveData

    public Application getApplication() { // Add this method
        return application;
    }


    public LiveData<List<ImageEntity>> getImageUrisLiveData() {
        SharedPreferences preferences = getApplication().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userId = preferences.getString("userId", null); // Get user ID
        Log.d("GalleryViewModel", "User ID: " + userId); // Log user ID
        return imageRepository.getImagesForUser(userId); // Fetch images for user
    }

    // Factory class to create GalleryViewModel with Application parameter
    public static class GalleryViewModelFactory implements ViewModelProvider.Factory {
        private final Application application;
        public GalleryViewModelFactory(Application application) {
            this.application = application;
        }

        @NonNull

        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(GalleryViewModel.class)) {
                return (T) new GalleryViewModel(application);
            }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
    }
}
