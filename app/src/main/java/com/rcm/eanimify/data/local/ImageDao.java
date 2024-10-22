package com.rcm.eanimify.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageDao {
    @Insert
    void insert(ImageEntity image);

    @Query("SELECT * FROM images WHERE user_id = :userId")
    LiveData<List<ImageEntity>> getImagesForUser(String userId);

    @Query("SELECT image_uri FROM images WHERE user_id = :userId")
    LiveData<List<String>> getImageUrisForUser(String userId);
//    LiveData<List<ImageEntity>> getAllImages();
}
