package com.rcm.eanimify.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rcm.eanimify.R;
import com.rcm.eanimify.animalPicture.ImageDisplayActivity;
import com.rcm.eanimify.data.local.ImageEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context context; // Add context for LayoutInflater
    private List<ImageEntity> images = new ArrayList<>();
    private List<Uri> imageUris = new ArrayList<>();
    public ImageAdapter(Context context) { // Add constructor
        this.context = context;
    }
    private List<ImageEntity> selectedImages = new ArrayList<>();
    private boolean isSelectionMode = false;

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }


    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageEntity image = images.get(position);
//        Uri fileUri = Uri.fromFile(new File(image.imageUri)); // Convert to file:// URI
        String imagePath = image.imageUri;
//        Glide.with(context)
//                .load(fileUri)
//                .into(holder.imageView);
        Uri fileUri = Uri.parse(imagePath);
        File imageFile = new File(Objects.requireNonNull(fileUri.getPath()));

        if (imageFile.exists()) {
            // Load the image using Glide if the file exists
            Glide.with(context)
                    .load(fileUri) // Use the URI directly
                    .into(holder.imageView);
        } else {
            // Handle the case where the file doesn't exist
            Glide.with(context)
                    .load(R.drawable.placeholder_image) // Replace with your placeholder drawable
                    .into(holder.imageView);
        }


        holder.checkBox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(selectedImages.contains(images.get(position)));

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                // Toggle checkbox state in selection mode
                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                if (holder.checkBox.isChecked()) {
                    selectedImages.add(images.get(position));
                } else {
                    selectedImages.remove(images.get(position));
                }
            } else {
                // Open ImageDisplayActivity in normal mode
                Intent intent = new Intent(context, ImageDisplayActivity.class);
                intent.putExtra("imageUri", Uri.fromFile(new File(images.get(position).imageUri)).toString());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                isSelectionMode = true;
                holder.checkBox.setChecked(true);
                selectedImages.add(images.get(position));
                notifyDataSetChanged(); // Update all items to show checkboxes
            }
            return true; // Consume the long press event
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(List<ImageEntity> imageUris) { // Change parameter type to List<Uri>
        this.images = imageUris;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CheckBox checkBox;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
    public List<ImageEntity> getSelectedImages() {
        return selectedImages;
    }

    public void clearSelection() {
        isSelectionMode = false;
        selectedImages.clear();
        notifyDataSetChanged();
    }
    public boolean isSelectionMode() {
        return isSelectionMode;
    }
}
