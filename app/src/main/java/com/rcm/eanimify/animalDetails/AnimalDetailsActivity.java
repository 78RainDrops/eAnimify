package com.rcm.eanimify.animalDetails;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.rcm.eanimify.databinding.ActivityAnimalDetailsBinding;

import com.rcm.eanimify.R;

public class AnimalDetailsActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAnimalDetailsBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView animalNameTextView, scientificNameTextView, descriptionTextView, endangerLevelTextView, familyNameTextView, provinceTextView, taxonomicGroupTextView;

    private ActivityAnimalDetailsBinding binding1;
    private ProgressBar progressBar;

    private ImageView imageView;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(AnimalDetailsActivity.this).clearDiskCache(); // Use 'AnimalDetailsActivity.this' as the context
            }
        }).start();
        binding = ActivityAnimalDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        // Set up the Toolbar
        setSupportActionBar(binding.toolbar1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        animalNameTextView = findViewById(R.id.animal_name_textview);
        scientificNameTextView = findViewById(R.id.scientific_name_textview);
        descriptionTextView = findViewById(R.id.description_textview);
        endangerLevelTextView = findViewById(R.id.endanger_level_textview);
        familyNameTextView = findViewById(R.id.family_name_textview);
        provinceTextView = findViewById(R.id.province_textview);
        taxonomicGroupTextView = findViewById(R.id.taxonomic_group_textview);

        imageView = findViewById(R.id.imageView3); // Initialize ImageView
        storage = FirebaseStorage.getInstance();

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        String capturedImageUri = getIntent().getStringExtra("CAPTURED_IMAGE_URI");
        String animalName = getIntent().getStringExtra("animal_name");

        // Display the captured image
        displayCapturedImage(capturedImageUri);

        if (animalName != null) {
            loadAnimalData(animalName); // Fetch animal details using animal_name
        } else {
            Log.e("AnimalDetailsActivity", "No animal name found in Intent");
        }

        // Check if user is already signed in, else perform anonymous sign-in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Successfully signed in
                    FirebaseUser user = auth.getCurrentUser();
                    Log.d("AnimalDetailsActivity", "Signed in anonymously as " + user.getUid());
                    fetchAnimalDetails();  // Call your fetch method after authentication
                } else {
                    // Sign-in failed
                    Log.e("AnimalDetailsActivity", "Anonymous sign-in failed", task.getException());
                }
            });
        } else {
            fetchAnimalDetails();  // Call fetch method if already signed in
        }


    }

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                this.getOnBackPressedDispatcher().onBackPressed(); // Call onBackPressed() to navigate back
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    private void fetchAnimalDetails() {
        // Your existing code to fetch data from Firestore and Firebase Storage
        String animalName = getIntent().getStringExtra("animalName");
        animalNameTextView.setText(animalName);

        db.collection("Endemic Animals")
                .whereEqualTo("Common Name", animalName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        // Extract animal details
                        String scientificName = document.getString("Scientific Name");
                        String description = document.getString("Description");
                        String endangerLevel = document.getString("Endanger Level");
                        String familyName = document.getString("Family Name");
                        String province = document.getString("Province");
                        String taxonomicGroup = document.getString("Taxonomic Group");

                        // Update UI elements with the extracted details
                        scientificNameTextView.setText(scientificName);
                        descriptionTextView.setText(description);
                        endangerLevelTextView.setText(endangerLevel);
                        familyNameTextView.setText(familyName);
                        provinceTextView.setText(province);
                        taxonomicGroupTextView.setText(taxonomicGroup);
                        // ... Update other TextViews for endangerLevel, familyName, province, taxonomicGroup ...

                        Log.d("AnimalDetailsActivity", "Successfully retrieved animal details");
                        // Update other TextViews here...

                        // Fetch image from Firebase Storage
                        fetchImage(animalName);
                    } else {
                        Log.d("AnimalDetailsActivity", "No animal found with name: " + animalName);
                    }
//                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Log.e("AnimalDetailsActivity", "Error fetching animal details", e);
                });
    }
    private void fetchImage(String animalName) {
        db.collection("Animal_ImagesV3")
                .whereEqualTo("animal_name", animalName)
                .get()
                .addOnSuccessListener(imageQueryDocumentSnapshots -> {
                    if (!imageQueryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot imageDocument = imageQueryDocumentSnapshots.getDocuments().get(0);
                        String imagePath = imageDocument.getString("image_url"); // Get the full Storage path

                        if (imagePath != null) {
                            // Convert HTTPS URL to GS URL if needed
                            if (imagePath.startsWith("https://storage.googleapis.com/")) {
                                String bucketName = "eanimifyapplication-32745.appspot.com"; // Your bucket name
                                String gsPath = imagePath.replace("https://storage.googleapis.com/" + bucketName + "/", "gs://" + bucketName + "/");
                                // Create a StorageReference using the GS path
                                StorageReference storageRef = storage.getReferenceFromUrl(gsPath);

                                storageRef.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Glide.with(this).load(uri).into(imageView);
                                            progressBar.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AnimalDetailsActivity", "Failed to get download URL", e);
                                            progressBar.setVisibility(View.GONE);
                                        });
                            } else {
                                // Create a StorageReference using the full GS path
                                StorageReference storageRef = storage.getReferenceFromUrl(imagePath);

                                storageRef.getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Glide.with(this).load(uri).into(imageView);
                                            progressBar.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AnimalDetailsActivity", "Failed to get download URL", e);
                                            progressBar.setVisibility(View.GONE);
                                        });
                            }
                        } else {
                            Log.e("AnimalDetailsActivity", "Image path is null for animal: " + animalName);
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e("AnimalDetailsActivity", "No image document found for animal: " + animalName);
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AnimalDetailsActivity", "Error querying Firestore for image", e);
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void displayCapturedImage(String capturedImageUri) {
        // Load the captured image into the ImageView (you can use Glide or Picasso)
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(capturedImageUri)  // Assuming the captured image is passed as a URI string
                .placeholder(R.drawable.ic_images)  // Optional placeholder image
                .into(imageView);
        progressBar.setVisibility(View.GONE);
    }

    private void loadAnimalData(String animalName) {
        // Query Firestore using animal_name to get animal details
        db.collection("Endemic Animals")
                .whereEqualTo("Common Name", animalName) // Use "Common Name" field
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        // Extract animal details
                        String scientificName = document.getString("Scientific Name");
                        String description = document.getString("Description");
                        String endangerLevel = document.getString("Endanger Level");
                        String familyName = document.getString("Family Name");
                        String province = document.getString("Province");
                        String taxonomicGroup = document.getString("Taxonomic Group");

                        // Set the fetched data to the TextViews
                        animalNameTextView.setText(animalName);
                        scientificNameTextView.setText(scientificName);
                        descriptionTextView.setText(description);
                        endangerLevelTextView.setText(endangerLevel);
                        familyNameTextView.setText(familyName);
                        provinceTextView.setText(province);
                        taxonomicGroupTextView.setText(taxonomicGroup);
                    }else{
                        Log.d("AnimalDetailsActivity", "No animal found with name: " + animalName);
                    }
        }).addOnFailureListener(e -> {
                    Log.e("AnimalDetailsActivity", "Error fetching animal details", e);        });
    }

//

}