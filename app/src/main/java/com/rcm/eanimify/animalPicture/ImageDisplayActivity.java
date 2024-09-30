package com.rcm.eanimify.animalPicture;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import com.rcm.eanimify.R;

public class ImageDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        if (getIntent().hasExtra("imageUri")) {
            Uri imageUri = (Uri) getIntent().getParcelableExtra("imageUri");

            if (imageUri != null) {
                ImageView imageView = findViewById(R.id.displayImageView);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView);
            } else {
                Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
        }

//        if (getIntent().hasExtra("imageUrl")) {
//            String imageUrl = getIntent().getStringExtra("imageUrl");
//
//            ImageView imageView = findViewById(R.id.displayImageView);
//            Glide.with(this)
//                    .load(imageUrl)
//                    .into(imageView);
//        } else {
//            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
//        }


//        if (getIntent().hasExtra("imageUri")) {
//            String imageUriString = getIntent().getStringExtra("imageUri");
//            Uri imageUri = Uri.parse(imageUriString);
//
//            // Display the image in an ImageView
//            ImageView imageView = findViewById(R.id.displayImageView);
//            imageView.setImageURI(imageUri);
//        } else {
//            // Handle case where image URI is not found
//            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
//        }

//        if (getIntent().hasExtra("imageUrl")) {
//            String imageUrl = getIntent().getStringExtra("imageUrl");
//
//            ImageView imageView = findViewById(R.id.displayImageView);
//            Glide.with(this)
//                    .load(imageUrl)
//                    .into(imageView);
//        } else {
//            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
//        }

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Enable the back button
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