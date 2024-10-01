package com.rcm.eanimify.animalPicture;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rcm.eanimify.R;
import com.rcm.eanimify.data.local.AppDatabase;
import com.rcm.eanimify.data.local.ImageEntity;

import java.io.ByteArrayOutputStream;

public class ImageDisplayActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        ImageView imageView = findViewById(R.id.displayImageView);
        Button saveButton = findViewById(R.id.saveButton);
        Button discardButton = findViewById(R.id.discardButton);

        if (getIntent().hasExtra("imageUri")) {
            String imageUriString = getIntent().getStringExtra("imageUri");
            final Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);

            db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "image-database").build();

            saveButton.setOnClickListener(v -> {
                if (getIntent().hasExtra("imageUri")) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();

                        ImageEntity image = new ImageEntity();
                        image.imageUri = imageUriString;
                        image.userId = userId;

                        new Thread(() -> {
                            db.imageDao().insert(image);
                            runOnUiThread(() -> Toast.makeText(ImageDisplayActivity.this, "Image saved to database", Toast.LENGTH_SHORT).show());
                        }).start();
                        finish();
                    } else {
                        // Handle case where user is not logged in
                        Toast.makeText(ImageDisplayActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            discardButton.setOnClickListener(v -> finish());
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
////package com.rcm.eanimify.animalPicture;
////
////import android.graphics.Bitmap;
////import android.graphics.BitmapFactory;
////import android.net.Uri;
////import android.os.Bundle;
////import android.provider.MediaStore;
////import android.view.MenuItem;
////import android.view.View;
////import android.widget.Button;
////import android.widget.ImageView;
////import android.widget.Toast;
////
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.appcompat.widget.Toolbar;
////import androidx.lifecycle.ViewModelProvider;
////
////import com.bumptech.glide.Glide;
////import com.rcm.eanimify.R;
////import com.rcm.eanimify.data.local.ImageEntity;
////import com.rcm.eanimify.ui.gallery.GalleryViewModel;
////
////import java.io.IOException;
////import java.io.InputStream;
////
////public class ImageDisplayActivity extends AppCompatActivity {
////
////    GalleryViewModel galleryViewModel;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_image_display);
////
////        ImageView imageView = findViewById(R.id.displayImageView);
////        Button saveButton = findViewById(R.id.saveButton);
////        Button discardButton = findViewById(R.id.discardButton);
////
////        // Initialize ViewModel
////        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
////
////        // Get imageUri from Intent
////        String imageUriString = getIntent().getStringExtra("imageUri");
////        if (imageUriString != null) {
////            Toast.makeText(this, "Received Image URI: " + imageUriString, Toast.LENGTH_SHORT).show();
////            Uri imageUri = Uri.parse(imageUriString);
////            if (imageUri != null) {
////                // Load image using Glide
////                Glide.with(this)
////                        .load(imageUri)
////                        .into(imageView);
////
////                // Alternatively, if you prefer using Bitmap:
////                /*
////                try {
////                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
////                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
////                    imageView.setImageBitmap(bitmap);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
////                }
////                */
////
////                saveButton.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        // To save image to Firestore or local DB, you need to handle it appropriately
////                        // Here, you may convert the imageUri to Bitmap or other forms as needed
////
////                        try {
////                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
////                            // Convert Bitmap to byte array
////                            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
////                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
////                            byte[] byteArray = stream.toByteArray();
////
////                            ImageEntity image = new ImageEntity();
////                            image.imageData = byteArray;
////
////                            galleryViewModel.insert(image);
////
////                            Toast.makeText(ImageDisplayActivity.this, "Image saved to database", Toast.LENGTH_SHORT).show();
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                            Toast.makeText(ImageDisplayActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });
////
////                discardButton.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        // Finish the activity
////                        finish();
////                    }
////                });
////            } else {
////                Toast.makeText(this, "Invalid image URI", Toast.LENGTH_SHORT).show();
////            }
////        } else {
////            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
////        }
////
////        Toolbar myToolbar = findViewById(R.id.my_toolbar);
////        setSupportActionBar(myToolbar);
////
////        if (getSupportActionBar() != null) {
////            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
////        }
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        if (item.getItemId() == android.R.id.home) {
////            onBackPressed();
////            return true;
////        }
////        return super.onOptionsItemSelected(item);
////    }
////}
//
//package com.rcm.eanimify.animalPicture;
//
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.bumptech.glide.Glide;
//
//import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream;
//import com.rcm.eanimify.R;
//import com.rcm.eanimify.data.local.ImageEntity;
//import com.rcm.eanimify.ui.gallery.GalleryViewModel;
//
//import java.io.IOException;
//import java.io.OutputStream;
//
//public class ImageDisplayActivity extends AppCompatActivity {
//
//    GalleryViewModel galleryViewModel;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_image_display);
//
//        ImageView imageView = findViewById(R.id.displayImageView);
//        Button saveButton = findViewById(R.id.saveButton);
//        Button discardButton = findViewById(R.id.discardButton);
//
//        Bitmap imageBitmap = (Bitmap) getIntent().getParcelableExtra("imageBitmap");
//        if (imageBitmap != null) {
//            imageView.setImageBitmap(imageBitmap);
//
//            saveButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (imageBitmap != null) {
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                        byte[] byteArray = stream.toByteArray();
//
//                        ImageEntity image = new ImageEntity();
//                        image.imageData = byteArray;
////
//                        galleryViewModel.insert(image);
//
//                        Toast.makeText(ImageDisplayActivity.this, "Image saved to database", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//            discardButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Finish the activity
//                    finish();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
//        }
//
//        Toolbar myToolbar = findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
////        if (getIntent().hasExtra("imageUri")) {
////            Uri imageUri = (Uri) getIntent().getParcelableExtra("imageUri");
////
////            if (imageUri != null) {
////                ImageView imageView = findViewById(R.id.displayImageView);
////                Glide.with(this)
////                        .load(imageUri)
////                        .into(imageView);
////            } else {
////                Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show();
////            }
////        } else {
////            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
////        }
//
////        if (getIntent().hasExtra("imageUrl")) {
////            String imageUrl = getIntent().getStringExtra("imageUrl");
////
////            ImageView imageView = findViewById(R.id.displayImageView);
////            Glide.with(this)
////                    .load(imageUrl)
////                    .into(imageView);
////        } else {
////            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
////        }
//
//
////        if (getIntent().hasExtra("imageUri")) {
////            String imageUriString = getIntent().getStringExtra("imageUri");
////            Uri imageUri = Uri.parse(imageUriString);
////
////            // Display the image in an ImageView
////            ImageView imageView = findViewById(R.id.displayImageView);
////            imageView.setImageURI(imageUri);
////        } else {
////            // Handle case where image URI is not found
////            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
////        }
//
////        if (getIntent().hasExtra("imageUrl")) {
////            String imageUrl = getIntent().getStringExtra("imageUrl");
////
////            ImageView imageView = findViewById(R.id.displayImageView);
////            Glide.with(this)
////                    .load(imageUrl)
////                    .into(imageView);
////        } else {
////            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
////        }
//
////        Toolbar myToolbar = findViewById(R.id.my_toolbar);
////        setSupportActionBar(myToolbar);
////
////        // Enable the back button
////        if (getSupportActionBar() != null) {
////            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
////        }
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
////        if (item.getItemId() == android.R.id.home) {
////            onBackPressed();
////            return true;
////        }
////        return super.onOptionsItemSelected(item);
//    }
//}