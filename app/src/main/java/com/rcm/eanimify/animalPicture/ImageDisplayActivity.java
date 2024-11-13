package com.rcm.eanimify.animalPicture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.room.Room;

import com.bumptech.glide.Glide;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.util.Executors;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.rcm.eanimify.R;
import com.rcm.eanimify.animalDetails.AnimalDetailsActivity;
import com.rcm.eanimify.data.local.AppDatabase;
import com.rcm.eanimify.data.local.ImageEntity;
import com.rcm.eanimify.model.TFLiteModelHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDisplayActivity extends AppCompatActivity {

    private AppDatabase db;
    private FirebaseFirestore database;
    private TFLiteModelHelper tfliteModelHelper;
    private Button searchButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_image_display);


        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "image-database").build();

        ImageView imageView = findViewById(R.id.displayImageView);
        Button saveButton = findViewById(R.id.saveButton);
        Button discardButton = findViewById(R.id.discardButton);
        tfliteModelHelper = new TFLiteModelHelper(this);
        database = FirebaseFirestore.getInstance();

        searchButton = findViewById(R.id.searchButton);

        if (getIntent().hasExtra("imageUri")) {
            String imageUriString = getIntent().getStringExtra("imageUri");
            imageUri = Uri.parse(imageUriString); // Assign to class-level imageUri

            Glide.with(this)
                    .load(imageUri)
                    .into(imageView);

            saveButton.setOnClickListener(v -> saveCapturedImage(imageUriString));

            searchButton.setOnClickListener(v -> searchForAnimal());
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
    private void saveCapturedImage(String imageUriString) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            File cacheDir = this.getDir("cache", Context.MODE_PRIVATE);
            String originalImageName = Uri.parse(imageUriString).getLastPathSegment();
            File imageFile = new File(cacheDir, originalImageName);
            String newFilePath = imageFile.getAbsolutePath();

            ImageEntity imageEntity = new ImageEntity();
            imageEntity.imageUri = newFilePath;
            imageEntity.userId = userId;

            saveImage(imageEntity);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
    private void searchForAnimal() {
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                float[] featureVector = tfliteModelHelper.classifyImage(bitmap);
                findClosestMatch(featureVector);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image to search", Toast.LENGTH_SHORT).show();
        }
    }
    private void findClosestMatch(float[] featureVector) {
        database.collection("Animal_ImagesV3")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double maxSimilarity = 0.0;
                    String bestMatchAnimalId = null;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        List<Double> dbFeatureVector = (List<Double>) document.get("average_feature_vector");
                        double similarity = calculateCosineSimilarity(featureVector, dbFeatureVector);

                        Log.d("ImageDisplayActivity", "Similarity with " + document.getString("animal_name") + ": " + similarity);

                        if (similarity > maxSimilarity) {
                            maxSimilarity = similarity;
                            bestMatchAnimalId = document.getString("animal_name");
                        }
                    }

                    // Confidence check: only proceed if similarity exceeds 80%
                    if (maxSimilarity > 0.8) {
                        if (bestMatchAnimalId != null) {
                            Log.d("ImageDisplayActivity", "Match found: " + bestMatchAnimalId + " with similarity: " + maxSimilarity);
                            Intent intent = new Intent(ImageDisplayActivity.this, AnimalDetailsActivity.class);
                            intent.putExtra("animal_name", bestMatchAnimalId);
                            intent.putExtra("CAPTURED_IMAGE_URI", imageUri.toString());
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "No matching animal found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No animal match found with sufficient confidence", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ImageDisplayActivity", "Error finding closest match", e);
                });
    }


    private double calculateCosineSimilarity(float[] vec1, List<Double> vec2) {
        double dotProduct = 0.0;
        double normVec1 = 0.0;
        double normVec2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2.get(i);
            normVec1 += vec1[i] * vec1[i];
            normVec2 += vec2.get(i) * vec2.get(i);
        }
        return dotProduct / (Math.sqrt(normVec1) * Math.sqrt(normVec2));
    }

//    private void displayAnimalInfo(String animalName) {
//        if (animalName != null) {
//            database.collection("Animal_ImagesV2")
//                    .whereEqualTo("animal_name", animalName)
//                    .get()
//                    .addOnSuccessListener(doc -> {
//                        if (!doc.isEmpty()) {
//                            String animalDetails = doc.getDocuments().get(0).getString("details");
//                            showAnimalDetails(animalDetails);
//                        }
//                    });
//        }
//    }

//    private void showAnimalDetails(String details) {
//        Intent intent = new Intent(this, AnimalDetailsActivity.class);
//        intent.putExtra("CAPTURED_IMAGE_URI", capturedImageUri); // The URI of the captured image
//        intent.putExtra("MATCHED_ANIMAL_ID", matchedAnimalId);   // The ID of the matched animal from Firestore
//        startActivity(intent);
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//    private void saveImage(ImageEntity imageEntity) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//            // Perform database insertion in the background thread
//            db.imageDao().insert(imageEntity);
//
//            // Update UI on the main thread (if needed)
//            runOnUiThread(() -> {
//                Toast.makeText(ImageDisplayActivity.this, "Image saved to database", Toast.LENGTH_SHORT).show();
//                finish(); // Finish activity after database operation
//            });
//        });
//    }
    private void saveImage(ImageEntity imageEntity) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            db.imageDao().insert(imageEntity);
            runOnUiThread(() -> {
                Toast.makeText(this, "Image saved to database", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
}

}
