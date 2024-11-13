package com.rcm.eanimify.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rcm.eanimify.R;
import com.rcm.eanimify.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setFontSize(sharedPreferences);

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    private void setFontSize(SharedPreferences sharedPreferences) {
        int fontSize = sharedPreferences.getInt("font_size", 16); // Default to 16sp
        binding.textHome.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setFontSize(sharedPreferences);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this);
        binding = null;
    }

}