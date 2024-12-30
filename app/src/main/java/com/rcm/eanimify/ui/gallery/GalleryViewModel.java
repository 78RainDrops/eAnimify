package com.rcm.eanimify.ui.gallery;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.rcm.eanimify.data.local.AppDatabase;
import com.rcm.eanimify.data.local.ImageDao;
import com.rcm.eanimify.data.local.ImageEntity;
import com.rcm.eanimify.data.local.ImageRepository;

import java.io.File;
import java.util.List;

public class GalleryViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final ImageRepository imageRepository;
    private final MutableLiveData<String> mText;
    private MutableLiveData<List<ImageEntity>> imageUrisLiveData;
    private LiveData<List<ImageEntity>> roomLiveData;
    private final ImageDao imageDao;
    private final Application application;
    String userId;

    public GalleryViewModel(Application application) {
        super();
        this.application = application;
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");

        // Initialize imageRepository and imageDao
        imageRepository = new ImageRepository(application);
        imageDao = AppDatabase.getDatabase(application).imageDao();// Initialize imageRepository
        SharedPreferences preferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId  = preferences.getString("userId", null);

//        imageDao = imageRepository.getImageDao();
//        LiveData<List<ImageEntity>> allImages = imageRepository.getImagesForUser(userId);

//        imageUrisLiveData = imageRepository.getImagesForUser(userId);

        roomLiveData = imageRepository.getImagesForUser(userId);
        imageUrisLiveData = new MutableLiveData<>();

        // Observe changes in Room's LiveData and update MutableLiveData
        roomLiveData.observeForever(imageEntities -> imageUrisLiveData.postValue(imageEntities));
    }
    public LiveData<List<ImageEntity>> getImageUrisLiveData() {
        return imageUrisLiveData;
    }

//    public void deleteImage(String imageUri) {
//        // Check if the imageUri starts with "file:" and remove it for file operations
//        String filePath = imageUri.startsWith("file:") ? imageUri.substring(5) : imageUri; // Remove "file:" prefix
//
//        File file = new File(filePath); // Create File object without "file:" prefix
//        Log.d("DeleteImage", "Attempting to delete file at path: " + file.getAbsolutePath()); // Log the path
//
//        if (file.exists()) {
//            boolean deleted = file.delete();
//            if (deleted) {
//                Log.d("DeleteImage", "File deleted successfully: " + filePath);
//                new Thread(() -> {
//                    // Use the original imageUri with "file:" prefix for database deletion
//                    imageDao.deleteImageByUri(imageUri); // Ensure to use the correct URI format for the database
//
//                }).start();
//            } else {
//                Log.e("DeleteImage", "Failed to delete file: " + filePath);
//            }
//        } else {
//            Log.e("DeleteImage", "File not found at path: " + filePath);
//        }
//    }edit
public void deleteImage(String imageUri) {
    // Strip the "file:" prefix only for file deletion
    String filePath = imageUri.startsWith("file:") ? imageUri.substring(5) : imageUri;

    File file = new File(filePath); // File object without "file:" prefix
    if (file.exists()) {
        if (file.delete()) {
            Log.d("DeleteImage", "File deleted successfully: " + filePath);

            new Thread(() -> {
                // Use the original imageUri (with "file:") for database operations
                imageDao.deleteImageByUri(imageUri);
            }).start();
        } else {
            Log.e("DeleteImage", "Failed to delete file: " + filePath);
        }
    } else {
        Log.e("DeleteImage", "File not found at path: " + filePath);
    }
}


    public LiveData<String> getText() {
        return mText;
    }
    // Add this method to expose imageUrisLiveData

    public Application getApplication() { // Add this method
        return application;
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
