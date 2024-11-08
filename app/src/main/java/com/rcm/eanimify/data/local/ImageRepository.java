package com.rcm.eanimify.data.local;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class ImageRepository {
    private ImageDao imageDao;
    private LiveData<List<ImageEntity>> allImages;

    public ImageRepository(Application application) {

        SharedPreferences preferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);        String userId = preferences.getString("userId", null);
        AppDatabase db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "image-database").build();
        imageDao = db.imageDao();
        allImages = imageDao.getImagesForUser(userId); // Assuming you have this method in your DAO
    }

    public LiveData<List<ImageEntity>> getImagesForUser(String userId) {
        return imageDao.getImagesForUser(userId);
    }

    public void insert(ImageEntity image) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            imageDao.insert(image);
            Log.d("ImageRepository", "Inserted image: " + image.imageUri + " for user: " + image.userId);
        });
    }

}

