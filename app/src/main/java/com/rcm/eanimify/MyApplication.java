package com.rcm.eanimify;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceManager;

import com.google.firebase.FirebaseApp;
import com.rcm.eanimify.ui.animalLibrary.AnimalLibraryViewModel;

import java.util.Locale;

public class MyApplication extends Application implements ViewModelStoreOwner {

    private AnimalLibraryViewModel sharedViewModel;
    private ViewModelStore appViewModelStore;
    private MutableLiveData<Locale> localeLiveData = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Create shared ViewModel
        sharedViewModel = new ViewModelProvider(this).get(AnimalLibraryViewModel.class);
//        sharedViewModel.fetchDataFromFirebase();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false);
        String themeValue = sharedPreferences.getString("theme", "light");

//        int themeMode = isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
//        AppCompatDelegate.setDefaultNightMode(themeMode);
        // Apply theme
        if (themeValue.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (themeValue.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public AnimalLibraryViewModel getSharedViewModel() {
        return sharedViewModel;
    }

    @Override
    public ViewModelStore getViewModelStore() {
        if (appViewModelStore == null) {
            appViewModelStore = new ViewModelStore();
        }
        return appViewModelStore;
    }
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateBaseContextLocale(base));
    }
    private Context updateBaseContextLocale(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = sharedPreferences.getString("language", "en"); // Default to English

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }

    public void updateAppLocale(String language) {
        // This method is now called from SettingsFragment to trigger locale update
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        localeLiveData.setValue(locale); // Notify observers of locale change
    }

    public LiveData<Locale> getLocaleLiveData() {
        return localeLiveData;
    }
}
