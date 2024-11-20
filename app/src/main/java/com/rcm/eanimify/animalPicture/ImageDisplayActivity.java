package com.rcm.eanimify.animalPicture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "image-database").build();
        database = FirebaseFirestore.getInstance();
        tfliteModelHelper = new TFLiteModelHelper(this);

        ImageView imageView = findViewById(R.id.displayImageView);
        Button saveButton = findViewById(R.id.saveButton);
        Button discardButton = findViewById(R.id.discardButton);
        searchButton = findViewById(R.id.searchButton);

        if (getIntent().hasExtra("imageUri")) {
            String imageUriString = getIntent().getStringExtra("imageUri");
            imageUri = Uri.parse(imageUriString);

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

            ImageEntity imageEntity = new ImageEntity();
            imageEntity.imageUri = imageUriString;
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

                if (featureVector != null) {
                    findClosestMatch(featureVector);
                } else {
                    Toast.makeText(this, "Feature extraction failed", Toast.LENGTH_SHORT).show();
                }
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

                        if (dbFeatureVector != null && dbFeatureVector.size() == featureVector.length) {
                            double similarity = calculateCosineSimilarity(featureVector, dbFeatureVector);

                            Log.d("ImageDisplayActivity", "Similarity with " + document.getString("animal_name") + ": " + similarity);

                            if (similarity > maxSimilarity) {
                                maxSimilarity = similarity;
                                bestMatchAnimalId = document.getString("animal_name");
                            }
                        } else {
                            Log.w("ImageDisplayActivity", "Feature vector size mismatch or null for " + document.getString("animal_name"));
                        }
                    }

                    if (maxSimilarity > 0.7) {
                        if (bestMatchAnimalId != null) {
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
                .addOnFailureListener(e -> Log.e("ImageDisplayActivity", "Error finding closest match", e));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
