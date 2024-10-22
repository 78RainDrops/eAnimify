package com.rcm.eanimify.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rcm.eanimify.R;
import com.rcm.eanimify.data.local.ImageEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context context; // Add context for LayoutInflater
    private List<ImageEntity> images = new ArrayList<>();
    private List<Uri> imageUris = new ArrayList<>();
    public ImageAdapter(Context context) { // Add constructor
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
//        ImageEntity image = images.get(position);
//        Glide.with(context)
//                .load(Uri.parse(image.imageUri))
////                .placeholder(R.drawable.placeholder_image) // Add a placeholder
////                .error(R.drawable.error_image) // Add an error image
//                .into(holder.imageView);
//    }

    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageEntity image = images.get(position);
        Uri fileUri = Uri.fromFile(new File(image.imageUri)); // Convert to file:// URI
        String imagePath = image.imageUri;
//        Glide.with(context)
//                .load(fileUri)
//                .into(holder.imageView);
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            // Load the image using Glide if the file exists
            Glide.with(context)
                    .load(imageFile)
                    .into(holder.imageView);
        } else {
            // Handle the case where the file doesn't exist
            // e.g., display a placeholder image or show an error message
            Glide.with(context)
                    .load(R.drawable.placeholder_image) // Replace with your placeholder drawable
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

//    public void setImages(List<ImageEntity> images) {
//        this.images = images;
//        notifyDataSetChanged();
//    }
    public void setImages(List<ImageEntity> imageUris) { // Change parameter type to List<Uri>
        this.images = imageUris;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
