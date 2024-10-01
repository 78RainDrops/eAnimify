package com.rcm.eanimify;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.MenuItem;

import com.android.identity.util.UUID;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rcm.eanimify.Account.LoginActivity;

import com.bumptech.glide.Glide;

import com.rcm.eanimify.databinding.ActivityMainBinding;
import com.rcm.eanimify.animalPicture.ImageDisplayActivity;

import com.android.identity.util.UUID;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //    private static final String TAG = "MainActivity";
    private static final int CAMERA_REQUEST_CODE = 100;

    FirebaseAuth auth;

    //    TextView userDetailsTextView;
    FirebaseUser user;

//    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private ActivityMainBinding binding;
    ActivityResultLauncher<Uri> takePictureLauncher;
    //    private ActivityResultLauncher<Void> takePictureLauncher;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appbar.toolbar);

//    //for camera

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean o) {
                        try {
                            if (o) {
                                // Image captured successfully, now start ImageDisplayActivity
                                Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
                                intent.putExtra("imageUri", imageUri.toString());

                                // Grant temporary permission if using FileProvider
                                grantUriPermission(
                                        "com.rcm.eanimify.animalPicture.ImageDisplayActivity",
                                        imageUri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );

                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                });
//        takePictureLauncher = registerForActivityResult(
//                new ActivityResultContracts.TakePicture(),
//                new ActivityResultCallback<Boolean>() {
//                    @Override
//                    public void onActivityResult(Boolean o) {
//                        try {
//                            if (o) {
//                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                                    // User is signed in, proceed with upload
//                                    StorageReference storageRef = storage.getReference();
//                                    StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment());
//
//                                    // Add userId to Storage Metadata
//                                    UploadTask uploadTask = imageRef.putFile(imageUri, new StorageMetadata.Builder()
//                                            .setCustomMetadata("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
//                                            .build());
//
//                                    uploadTask.addOnSuccessListener(taskSnapshot -> {
//                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                                            String downloadUrl = uri.toString();
//                                            storeImageDetailsInFirestore(downloadUrl);
//
//                                            // Start ImageDisplayActivity with the download URL
//                                            Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
//                                            intent.putExtra("imageUrl", downloadUrl);
//                                            startActivity(intent);
//                                        });
//                                    }).addOnFailureListener(e -> {
//                                        Toast.makeText(MainActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
//                                    });
//                                } else {
//                                    // User is not signed in, handle accordingly (e.g., show sign-in prompt)
//                                    Toast.makeText(MainActivity.this, "Please sign in to upload the image", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Navigation function
        // Find the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Find the LinearLayout containing your buttons
//        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);

        // Find the buttons within the LinearLayout
        Button button1 = findViewById(R.id.btn_home);
        Button button2 = findViewById(R.id.btn_gallery);
        Button button3 = findViewById(R.id.btn_animal_library);

        // Set OnClickListeners for each button
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_home);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_gallery);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_animal_library);
            }
        });
        // ... other button click listeners ...

        // Configure AppBarConfiguration without DrawerLayout
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_animal_library)
                .build();

        // Set up ActionBar with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

//        camera function
        FloatingActionButton cameraButton = findViewById(R.id.camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

//        imageUri = createUri();
//        registerPictureLauncher();
        binding.appbar.camera.setOnClickListener(view -> {
            checkCameraPermissionAndOpenCamera();
        });
    }
//    for camera

    private void storeImageDetailsInFirestore(String downloadUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Generate a unique ID for the image
        String imageId = java.util.UUID.randomUUID().toString();

        // Get a name for the image (you might want to prompt the user for this)
        String imageName = "Image_" + imageId; // Or get it from user input

        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imageUrl", downloadUrl);
        imageData.put("userId", userId);
        imageData.put("imageId", imageId);
        imageData.put("imageName", imageName);

        db.collection("images")
                .document(imageId) // Use imageId as the document ID
                .set(imageData)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
    private Uri createUri() {
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Uri imageCollection;
        ContentValues imageDetails = new ContentValues();

        // Generate a unique file name using a timestamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, "image_" + timestamp + ".jpg");
        imageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        return resolver.insert(imageCollection, imageDetails);
    }

    //    private void checkCameraPermissionAndOpenCamera(){
//        if(ActivityCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//        }else takePictureLauncher.launch(imageUri); // Call launch() without arguments
//    }
    private void checkCameraPermissionAndOpenCamera(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            openCamera(); // Directly open the camera
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_CODE){
            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(); // Directly open the camera
            } else {
                Toast.makeText(this, "Permission Denied, please allow to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == CAMERA_REQUEST_CODE){
//            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                takePictureLauncher.launch(imageUri); // Remove imageUri
//            } else {
//                Toast.makeText(this, "Permission Denied, please allow to use camera", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
//            if (imageBitmap != null) {
//                Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
//                intent.putExtra("imageBitmap", imageBitmap);
//                startActivity(intent);
//            } else {
//                Toast.makeText(MainActivity.this, "Image capture failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (imageBitmap != null) {
                try {
                    // Create a temporary file to store the bitmap
                    File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();

                    // Pass the file URI to ImageDisplayActivity
                    Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
                    intent.putExtra("imageUri", tempFile.toURI().toString());
                    startActivity(intent);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(MainActivity.this, "Image capture failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //for menu or the 3 horizontal doTS IN the top right of the appbaR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu); // Use R.menu.main
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        user = auth.getCurrentUser();
        if (user == null) {
            goToLoginActivity();
        } else {
            if (user.isEmailVerified()) {
                if (!isLoggedIn) {
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Set the flag in SharedPreferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                }
                //                fetchUserDetails();
            } else {
                Toast.makeText(MainActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                auth.signOut();
                goToLoginActivity();
            }
        }
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void signOut() {
        auth.signOut();

        // Clear SharedPreferences
        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        goToLoginActivity();
    }
}
//package com.rcm.eanimify;
//
//import android.app.Activity;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.Menu;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//import android.Manifest;
//
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import androidx.navigation.ui.AppBarConfiguration;
//import androidx.navigation.ui.NavigationUI;
//
//import android.view.MenuItem;
//
//import com.android.identity.util.UUID;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageMetadata;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.rcm.eanimify.Account.LoginActivity;
//
//import com.bumptech.glide.Glide;
//
//import com.rcm.eanimify.databinding.ActivityMainBinding;
//import com.rcm.eanimify.animalPicture.ImageDisplayActivity;
//
//import com.android.identity.util.UUID;
//
//import java.io.ByteArrayOutputStream;
//import java.util.HashMap;
//import java.util.Map;
//
//public class MainActivity extends AppCompatActivity {
//
//    //    private static final String TAG = "MainActivity";
//    private static final int CAMERA_REQUEST_CODE = 100;
//
//    FirebaseAuth auth;
//
//    //    TextView userDetailsTextView;
//    FirebaseUser user;
//
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    private FirebaseStorage storage = FirebaseStorage.getInstance();
//
//    private ActivityMainBinding binding;
//    ActivityResultLauncher<Uri> takePictureLauncher;
//    //    private ActivityResultLauncher<Void> takePictureLauncher;
//    Uri imageUri;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        auth = FirebaseAuth.getInstance();
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.appbar.toolbar);
//
////    //for camera
//
//        takePictureLauncher = registerForActivityResult(
//                new ActivityResultContracts.TakePicture(),
//                new ActivityResultCallback<Boolean>() {
//                    @Override
//                    public void onActivityResult(Boolean o) {
//                        try {
//                            if (o) {
//                                // Image captured successfully, now start ImageDisplayActivity
//                                Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
//                                intent.putExtra("imageUri", imageUri.toString());
//
//                                // Grant temporary permission if using FileProvider
//                                grantUriPermission(
//                                        "com.rcm.eanimify.animalPicture.ImageDisplayActivity",
//                                        imageUri,
//                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
//                                );
//
//                                startActivity(intent);
//                            }
//                        } catch (Exception e) {
//                            e.getStackTrace();
//                        }
//                    }
//                });
////        takePictureLauncher = registerForActivityResult(
////                new ActivityResultContracts.TakePicture(),
////                new ActivityResultCallback<Boolean>() {
////                    @Override
////                    public void onActivityResult(Boolean o) {
////                        try {
////                            if (o) {
////                                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
////                                    // User is signed in, proceed with upload
////                                    StorageReference storageRef = storage.getReference();
////                                    StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment());
////
////                                    // Add userId to Storage Metadata
////                                    UploadTask uploadTask = imageRef.putFile(imageUri, new StorageMetadata.Builder()
////                                            .setCustomMetadata("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
////                                            .build());
////
////                                    uploadTask.addOnSuccessListener(taskSnapshot -> {
////                                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
////                                            String downloadUrl = uri.toString();
////                                            storeImageDetailsInFirestore(downloadUrl);
////
////                                            // Start ImageDisplayActivity with the download URL
////                                            Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
////                                            intent.putExtra("imageUrl", downloadUrl);
////                                            startActivity(intent);
////                                        });
////                                    }).addOnFailureListener(e -> {
////                                        Toast.makeText(MainActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
////                                    });
////                                } else {
////                                    // User is not signed in, handle accordingly (e.g., show sign-in prompt)
////                                    Toast.makeText(MainActivity.this, "Please sign in to upload the image", Toast.LENGTH_SHORT).show();
////                                }
////                            }
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                        }
////                    }
////                });
//
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        //Navigation function
//        // Find the NavController
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//
//        // Find the LinearLayout containing your buttons
////        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
//
//        // Find the buttons within the LinearLayout
//        Button button1 = findViewById(R.id.btn_home);
//        Button button2 = findViewById(R.id.btn_gallery);
//        Button button3 = findViewById(R.id.btn_animal_library);
//
//        // Set OnClickListeners for each button
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.nav_home);
//            }
//        });
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.nav_gallery);
//            }
//        });
//
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.nav_animal_library);
//            }
//        });
//        // ... other button click listeners ...
//
//        // Configure AppBarConfiguration without DrawerLayout
//        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_animal_library)
//                .build();
//
//        // Set up ActionBar with NavController
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//
////        camera function
//        FloatingActionButton cameraButton = findViewById(R.id.camera);
//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openCamera();
//            }
//        });
//
////        imageUri = createUri();
////        registerPictureLauncher();
//        binding.appbar.camera.setOnClickListener(view -> {
//            checkCameraPermissionAndOpenCamera();
//        });
//    }
////    for camera
//
//    private void storeImageDetailsInFirestore(String downloadUrl) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        // Generate a unique ID for the image
//        String imageId = java.util.UUID.randomUUID().toString();
//
//        // Get a name for the image (you might want to prompt the user for this)
//        String imageName = "Image_" + imageId; // Or get it from user input
//
//        Map<String, Object> imageData = new HashMap<>();
//        imageData.put("imageUrl", downloadUrl);
//        imageData.put("userId", userId);
//        imageData.put("imageId", imageId);
//        imageData.put("imageName", imageName);
//
//        db.collection("images")
//                .document(imageId) // Use imageId as the document ID
//                .set(imageData)
//                .addOnSuccessListener(aVoid -> {
//                    // Handle success
//                })
//                .addOnFailureListener(e -> {
//                    // Handle failure
//                });
//    }
//    private Uri createUri() {
//        ContentResolver resolver = getApplicationContext().getContentResolver();
//        Uri imageCollection;
//        ContentValues imageDetails = new ContentValues();
//
//        // Generate a unique file name using a timestamp
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, "image_" + timestamp + ".jpg");
//        imageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//        } else {
//            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        }
//        return resolver.insert(imageCollection, imageDetails);
//    }
//
//    //    private void checkCameraPermissionAndOpenCamera(){
////        if(ActivityCompat.checkSelfPermission(MainActivity.this,
////                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
////            ActivityCompat.requestPermissions(MainActivity.this,
////                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
////        }else takePictureLauncher.launch(imageUri); // Call launch() without arguments
////    }
//    private void checkCameraPermissionAndOpenCamera(){
//        if(ActivityCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
//        } else {
//            openCamera(); // Directly open the camera
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == CAMERA_REQUEST_CODE){
//            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openCamera(); // Directly open the camera
//            } else {
//                Toast.makeText(this, "Permission Denied, please allow to use camera", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
////    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        if(requestCode == CAMERA_REQUEST_CODE){
////            if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                takePictureLauncher.launch(imageUri); // Remove imageUri
////            } else {
////                Toast.makeText(this, "Permission Denied, please allow to use camera", Toast.LENGTH_SHORT).show();
////            }
////        }
////    }
//
//    private void openCamera() {
//        // Create a URI for the image to be captured
//        imageUri = createImageUri();
//
//        if(imageUri != null){
//            // Launch the camera to take a picture
//            takePictureLauncher.launch(imageUri);
//        } else {
//            Toast.makeText(this, "Unable to create image file.", Toast.LENGTH_SHORT).show();
//        }
////        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        startActivityForResult(intent, CAMERA_REQUEST_CODE);
//////
//    }
//    private Uri createImageUri() {
//        ContentResolver resolver = getContentResolver();
//        Uri imageCollection;
//        ContentValues imageDetails = new ContentValues();
//
//        // Generate a unique file name using a timestamp
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, "image_" + timestamp + ".jpg");
//        imageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//        } else {
//            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        }
//
//        Uri uri = resolver.insert(imageCollection, imageDetails);
//        if(uri == null){
//            Toast.makeText(this, "Failed to create URI for image", Toast.LENGTH_SHORT).show();
//        }
//        return uri;
//    }
//
//
////    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
////            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
////            if (imageBitmap != null) {
////                Intent intent = new Intent(MainActivity.this, ImageDisplayActivity.class);
////                intent.putExtra("imageBitmap", imageBitmap);
////                startActivity(intent);
////            } else {
////                Toast.makeText(MainActivity.this, "Image capture failed", Toast.LENGTH_SHORT).show();
////            }
////        }
////    }
//
//
//    //for menu or the 3 horizontal doTS IN the top right of the appbaR
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu); // Use R.menu.main
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.sign_out) {
//            signOut();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
//        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
//
//        user = auth.getCurrentUser();
//        if (user == null) {
//            goToLoginActivity();
//        } else {
//            if (user.isEmailVerified()) {
//                if (!isLoggedIn) {
//                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//
//                    // Set the flag in SharedPreferences
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putBoolean("isLoggedIn", true);
//                    editor.apply();
//                }
//                //                fetchUserDetails();
//            } else {
//                Toast.makeText(MainActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
//                auth.signOut();
//                goToLoginActivity();
//            }
//        }
//    }
//
//    private void goToLoginActivity() {
//        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void signOut() {
//        auth.signOut();
//
//        // Clear SharedPreferences
//        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
//        editor.apply();
//
//        goToLoginActivity();
//    }
//}