package com.rcm.eanimify.data.local;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import java.util.List;

public class ImageRepository {
    private ImageDao imageDao;
    private LiveData<List<ImageEntity>> allImages;
    SharedPreferences preferences;
    String userId;

    public ImageRepository(Application application) {

        preferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = preferences.getString("userId", null);
//        AppDatabase db = Room.databaseBuilder(application.getApplicationContext(),
//                AppDatabase.class, "image-database").build();
//        imageDao = db.imageDao();
//        allImages = imageDao.getImagesForUser(userId); // Assuming you have this method in your DAO
        if (userId != null) {
            // Proceed only if userId is not null
            AppDatabase db = Room.databaseBuilder(application.getApplicationContext(),
                    AppDatabase.class, "image-database").build();
            imageDao = db.imageDao();
            allImages = imageDao.getImagesForUser(userId);
        } else {
            // Handle the case where userId is null
            Log.e("ImageRepository", "User ID is nu ll. Unable to fetch images.");
        }
    }

//    public LiveData<List<ImageEntity>> getImagesForUser(String userId) {
//        return imageDao.getImagesForUser(userId);
//    }
    public LiveData<List<ImageEntity>> getImagesForUser(String userId) {
        if (userId != null) {
            return imageDao.getImagesForUser(userId); // Ensure userId is passed here
        } else {
            Log.e("ImageRepository", "User ID is null. Unable to fetch images.");
            return new MutableLiveData<>();  // Return empty LiveData or handle appropriately
        }
    }

//    public void insert(ImageEntity image) {
//        AppDatabase.databaseWriteExecutor.execute(() -> {
//            imageDao.insert(image);
//            Log.d("ImageRepository", "Inserted image: " + image.imageUri + " for user: " + image.userId);
//        });
//    }
    public ImageDao getImageDao() {
        return imageDao;
    }

}

