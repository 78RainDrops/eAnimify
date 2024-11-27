package com.rcm.eanimify.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.appcheck.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rcm.eanimify.R;
import com.rcm.eanimify.policy.PrivacyPolicyActivity;
import com.rcm.eanimify.termsOfService.TermsOfServiceActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        ListPreference themePreference = findPreference("theme");
        assert themePreference != null;
        themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String themeValue = (String) newValue; // Get the String value

            // Apply the theme based on the selected value
            if (themeValue.equals("light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (themeValue.equals("dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            // Store theme preference
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            sharedPreferences.edit().putString("theme", themeValue).apply(); // Store as String

            // Recreate activity or fragments
            requireActivity().recreate(); // Or use a more targeted approach

            return true;
        });

        Preference fontSizePreference = findPreference("font_size");
        fontSizePreference.setOnPreferenceClickListener(preference -> {
            // Show font size selection dialog
            showFontSizeDialog();
            return true;
        });

        ListPreference languagePreference = findPreference("language");
        languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String language = (String) newValue;

            // Store language preference
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            sharedPreferences.edit().putString("language", language).apply();

            // Update locale and recreate activity
            updateLocale(language);
            requireActivity().recreate(); // Or use a more targeted approach

            return true;
        });
        Preference appVersionPreference = findPreference("about_app");
        appVersionPreference.setSummary(BuildConfig.VERSION_NAME);

         // Handle open source licenses
        Preference openSourceLicensesPreference = findPreference("about_app");
        openSourceLicensesPreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), OssLicensesMenuActivity.class));
            return true;
        });

        Preference privacyPolicyPreference = findPreference("privacy_policy");
        privacyPolicyPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(requireContext(), PrivacyPolicyActivity.class);
            startActivity(intent);
            return true;
        });

        Preference feedbackPreference = findPreference("feedback");
        if (feedbackPreference != null) {
            feedbackPreference.setOnPreferenceClickListener(preference -> {
                // Handle feedback preference click
                showFeedbackDialog();
                return true;
            });

        };
        Preference termsOfServicePreference = findPreference("terms_of_service");
        if (termsOfServicePreference != null) {
            termsOfServicePreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(requireContext(), TermsOfServiceActivity.class);
                startActivity(intent);
                return true;
            });
        }

    }
    private void showFontSizeDialog() {
        // Create an AlertDialog with font size options
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Font Size");

        // Define font size options (you can customize these)
        final String[] fontSizes = {"Small", "Medium", "Large"};
        final int[] fontSizeValues = {12, 16, 20};

        builder.setItems(fontSizes, (dialog, which) -> {
            int selectedFontSize = fontSizeValues[which];

            // Store font size preference
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            sharedPreferences.edit().putInt("font_size", selectedFontSize).apply();

            // Apply font size (you might need to recreate activities or fragments)
            // requireActivity().recreate(); // Or use a more targeted approach
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialogInterface -> {
            // Highlight the current font size when the dialog is first shown
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            int currentFontSize = sharedPreferences.getInt("font_size", 16); // Default to 16sp

            // Get the index of the current font size in the fontSizes array
            int selectedIndex = -1;
            for (int i = 0; i < fontSizeValues.length; i++) {
                if (fontSizeValues[i] == currentFontSize) { // Compare individual elements
                    selectedIndex = i;
                    break;
                }
            }

            // Highlight the selected item in the dialog (e.g., change background color)
            ListView listView = ((AlertDialog) dialogInterface).getListView();
            int finalSelectedIndex = selectedIndex;
            listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    listView.getViewTreeObserver().removeOnGlobalLayoutListener(this); // Remove listener after first layout

                    if (finalSelectedIndex != -1 && finalSelectedIndex < listView.getChildCount()) {
                        listView.getChildAt(finalSelectedIndex).setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlight_color)); // Replace with your highlight color
                    }
                }
            });
        });

        alertDialog.show();

//        builder.create().show();
    }

    public void updateLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());

    }
    public void showFeedbackDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Submit Feedback");

        final EditText input = new EditText(requireContext());
        input.setHint("Enter your feedback here");
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) ->{
            String feedbackText = input.getText().toString().trim();

            if (feedbackText.isEmpty()){
                Toast.makeText(requireContext(), "Feedback cannot be empty", Toast.LENGTH_SHORT).show();
            } else{
                saveFeedbackToFirestore(feedbackText);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void saveFeedbackToFirestore(String feedbackText){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser == null){
            Toast.makeText(requireContext(), "You must be logged in to submit feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("feedback", feedbackText);
        feedback.put("timestamp", FieldValue.serverTimestamp());
        feedback.put("userId", currentUser.getUid());
        feedback.put("email", currentUser.getEmail());

        db.collection("Feedback")
                .add(feedback)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(requireContext(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

}