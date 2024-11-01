package com.rcm.eanimify.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.preference.PreferenceManager;

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        // ... (other logic)

        // Apply font size from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int fontSize = sharedPreferences.getInt("font_size", 16); // Default to 16sp
        setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize); // Set text size in sp
    }

}
