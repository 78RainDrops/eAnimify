//package com.rcm.eanimify.data.local;
//
//import androidx.room.Entity;
//import androidx.room.PrimaryKey;
//
//@Entity(tableName = "images")
//public class ImageEntity {
//    @PrimaryKey(autoGenerate = true)
//    public int id;
//
//    public String imageId;
//    public String imageName;
//    public String userId;
//    public String imageUrl; // If storing URL from Firebase
//    public byte[] imageData; // If storing raw image data
//}

package com.rcm.eanimify.data.local;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

@Entity(tableName = "images")
public class ImageEntity {
    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "image_id")
    public int id;

//    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//    public byte[] imageData;

    @ColumnInfo(name = "image_uri")
    @NonNull
    public String imageUri;

    @ColumnInfo(name = "user_id")
    public String userId;

    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getImageUri() {
        return imageUri;
    }
}
