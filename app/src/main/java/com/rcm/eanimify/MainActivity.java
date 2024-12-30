package com.rcm.eanimify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rcm.eanimify.Account.ChangePasswordActivity;
import com.rcm.eanimify.Account.LoginActivity;

import com.rcm.eanimify.databinding.ActivityMainBinding;
import com.rcm.eanimify.animalPicture.ImageDisplayActivity;

import com.rcm.eanimify.settings.SettingsActivity;
import com.rcm.eanimify.ui.animalLibrary.AnimalLibraryViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //    private static final String TAG = "MainActivity";
    private static final int CAMERA_REQUEST_CODE = 100;

    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;


    private ActivityMainBinding binding;
    ActivityResultLauncher<Uri> takePictureLauncher;
    //    private ActivityResultLauncher<Void> takePictureLauncher;
    Uri imageUri;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private AnimalLibraryViewModel sharedViewModel;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private AppBarConfiguration mAppBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);

        getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());

        MyApplication myApplication = (MyApplication) getApplicationContext();
        sharedViewModel = myApplication.getSharedViewModel();


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = binding.main;

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, binding.appbar.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        setSupportActionBar(binding.appbar.toolbar);

//    //for camera

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // Handle the returned data here
                        if (data != null) {
                            Bitmap imageBitmap = data.getExtras().getParcelable("data");
                            if (imageBitmap != null) {
                                try {
                                    File cacheDir = getDir("cache", Context.MODE_PRIVATE);
                                    // Create a temporary file to store the bitmap
                                    File tempFile = File.createTempFile("temp_image", ".jpg", cacheDir);
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
                });

        setupUser();
//        setupNavigation();


    }
    private void setupUser() {
        user = auth.getCurrentUser ();
        if (user != null) {
            // Get the NavigationView
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0); // Get the header view

            // Find TextViews in the header layout
            TextView userNameTextView = headerView.findViewById(R.id.users_name);
            TextView userEmailTextView = headerView.findViewById(R.id.users_email);

            // Set user email
            userEmailTextView.setText(user.getEmail() != null ? user.getEmail() : "user@example.com");

            // Retrieve user details from Firestore
            DocumentReference userDocRef = db.collection("users").document(user.getUid());
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Retrieve user data
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");

                    // Set the full name in the TextView
                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                    userNameTextView.setText(fullName.trim()); // Trim to remove any extra spaces
                } else {
                    userNameTextView.setText("User  Name");
                }
            });
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Navigation function
        // Find the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Find the LinearLayout containing your buttons
//        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                navController.navigate(R.id.nav_home);
                drawerToggle.syncState();
            } else if (id == R.id.nav_gallery) {
                navController.navigate(R.id.nav_gallery);
                drawerToggle.syncState();
            } else if (id == R.id.nav_animal_library) {
                navController.navigate(R.id.nav_animal_library);
                drawerToggle.syncState();
            }else if (id == R.id.change_pass) {
                openChangePasswordDialog();
//                drawerToggle.syncState();
            }else if (id == R.id.deleteAcc) {
                confirmAndDeleteAccount();
//                drawerToggle.syncState();
            }


            // Close the drawer after item is selected
            drawerLayout.closeDrawers();
            return true;
        });


        // Find the buttons within the LinearLayout
        Button button1 = findViewById(R.id.btn_home);
        Button button2 = findViewById(R.id.btn_gallery);
        Button button3 = findViewById(R.id.btn_animal_library);

        // Set OnClickListeners for each button
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_home);
                drawerToggle.syncState();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_gallery);
                drawerToggle.syncState();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_animal_library);
                drawerToggle.syncState();
            }
        });
        // ... other button click listeners ...

        // Configure AppBarConfiguration without DrawerLayout
        mAppBarConfiguration = new AppBarConfiguration.Builder(
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


        drawerToggle.syncState();
    }

//change password
private void openChangePasswordDialog() {
    // Example: Show a dialog to input the new password
    new AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setMessage("This will open a Change Password screen.")
            .setPositiveButton("Proceed", (dialog, which) -> {
                // Launch ChangePasswordActivity
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
            })
            .setNegativeButton("Cancel", null)
            .show();
}
//delete account
private void confirmAndDeleteAccount() {
    new AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Yes, Delete", (dialog, which) -> {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    goToLoginActivity();
                                } else {
                                    Toast.makeText(this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "No user signed in.", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
}


//    for camera


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


    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    //for menu or the 3 horizontal doTS IN the top right of the appbaR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu); // Use R.menu.main
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if  (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.sign_out) {
            signOut();
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.change_pass) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
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
