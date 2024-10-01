package com.rcm.eanimify.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rcm.eanimify.R;
import com.rcm.eanimify.data.local.ImageEntity;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private Context context; // Add context for LayoutInflater
    private List<ImageEntity> images = new ArrayList<>();

    public ImageAdapter(Context context) { // Add constructor
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageEntity image = images.get(position);
        Glide.with(context)
                .load(image.imageUri)
                .into(holder.imageView);
    }

//    @Override
//    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
//        ImageEntity image = images.get(position);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(image.imageData, 0, image.imageData.length);
//        holder.imageView.setImageBitmap(bitmap); // Set bitmap to ImageView
//    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(List<ImageEntity> images) {
        this.images = images;
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
