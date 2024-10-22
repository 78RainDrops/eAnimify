package com.rcm.eanimify.animalPicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.io.File;

public class ImageDisplayActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);


        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "image-database").build();

        ImageView imageView = findViewById(R.id.displayImageView);
        Button saveButton = findViewById(R.id.saveButton);
        Button discardButton = findViewById(R.id.discardButton);

        if (getIntent().hasExtra("imageUri")) {
            String imageUriString = getIntent().getStringExtra("imageUri");
            final Uri imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);


//            saveButton.setOnClickListener(v -> {
//
//                if (getIntent().hasExtra("imageUri")) {
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if (user != null) {
//                        String userId = user.getUid();
//
//                        ImageEntity image = new ImageEntity();
//                        image.imageUri = imageUriString;
//                        image.userId = userId;
//
//                        new SaveImageTask().execute(image);
//
////                        new Thread(() -> {
////                            db.imageDao().insert(image);
////                            runOnUiThread(() -> Toast.makeText(ImageDisplayActivity.this, "Image saved to database", Toast.LENGTH_SHORT).show());
////                        }).start();
////                        finish();
//                    } else {
//                        // Handle case where user is not logged in
//                        Toast.makeText(ImageDisplayActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getIntent().hasExtra("imageUri")) {
                        String imageUriString = getIntent().getStringExtra("imageUri");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            // 1. Get the desired directory (cache directory within private storage)
                            File cacheDir = ImageDisplayActivity.this.getDir("cache", Context.MODE_PRIVATE);

                            // 2. Construct the file path using the original image name
                            String originalImageName = Uri.parse(imageUriString).getLastPathSegment();
                            File imageFile = new File(cacheDir, originalImageName);
                            String newFilePath = imageFile.getAbsolutePath(); // This will be /data/data/com.rcm.eanimify/cache/originalImageName

                            // 3. Create ImageEntity and save to database
                            ImageEntity imageEntity = new ImageEntity();
                            imageEntity.imageUri = newFilePath;
                            imageEntity.userId = userId;

                            new SaveImageTask().execute(imageEntity);

                        } else {
                            // Handle case where user is not logged in
                            Toast.makeText(ImageDisplayActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        }
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
//    private class SaveImageTask extends AsyncTask<ImageEntity, Void, Void> {
//
//        @Override
//        protected Void doInBackground(ImageEntity... images) {
//            db.imageDao().insert(images[0]);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            Toast.makeText(ImageDisplayActivity.this, "Image saved to database", Toast.LENGTH_SHORT).show();
//            finish(); // Finish activity after database operation
//        }
//    }
private class SaveImageTask extends AsyncTask<ImageEntity, Void, Void> {

    @Override
    protected Void doInBackground(ImageEntity... imageEntities) {
        db.imageDao().insert(imageEntities[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(ImageDisplayActivity.this, "Image saved to database", Toast.LENGTH_SHORT).show();
        finish(); // Finish activity after database operation
    }
}
}
