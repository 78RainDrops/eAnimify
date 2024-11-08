package com.rcm.eanimify.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageItemDecoration extends RecyclerView.ItemDecoration {

    private final int margin;

    public ImageItemDecoration(int margin){this.margin = margin;}

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(margin, margin, margin, margin);
    }
}
