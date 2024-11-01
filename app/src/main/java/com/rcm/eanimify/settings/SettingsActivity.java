package com.rcm.eanimify.settings;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.rcm.eanimify.MyApplication;
import com.rcm.eanimify.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Toolbar toolbar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
       getSupportFragmentManager()
               .beginTransaction()
               .replace(R.id.settings_container, new SettingsFragment())
               .commit();

        toolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); // Use getApplicationContext()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("language")) {
            int selectedPosition = sharedPreferences.getInt("language_position", 0); // Get selected position
            String[] languageValues = getResources().getStringArray(R.array.language_values);
            String languageCode = languageValues[selectedPosition]; // Get language code

            ((MyApplication) getApplication()).updateAppLocale(languageCode); // Update locale
            recreate(); // Recreate activity
        }
    }


//    private void updateAppLocale(String language) {
//        Locale locale = new Locale(language);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
//
//        // Recreate the activity to apply the locale change
//        recreate();
//    }


}