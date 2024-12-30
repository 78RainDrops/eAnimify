package com.rcm.eanimify.Account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rcm.eanimify.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText editNewPassword, editConfirmPassword;
    private TextView feedbackText;
    private Button btnChangePassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_change_password);

        Toolbar myToolbar = findViewById(R.id.my_toolbar2);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        feedbackText = findViewById(R.id.strong_password);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        auth = FirebaseAuth.getInstance();

        // Add TextWatcher for the "New Password" field
        editNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add TextWatcher for the "Confirm Password" field
        editConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newPassword = editNewPassword.getText().toString().trim();
                String confirmPassword = editConfirmPassword.getText().toString().trim();

                if (!confirmPassword.equals(newPassword)) {
                    feedbackText.setText(R.string.password_does_not_match);
                    feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else if (!newPassword.isEmpty()) {
                    checkPasswordStrength(newPassword);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle password change button click
        btnChangePassword.setOnClickListener(v -> {
            String newPassword = editNewPassword.getText().toString().trim();
            String confirmPassword = editConfirmPassword.getText().toString().trim();

            if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                editConfirmPassword.requestFocus();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                editConfirmPassword.requestFocus();
                return;
            }

            if (isPasswordStrong(newPassword)) {
                changePassword(newPassword);
            } else {
                Toast.makeText(this, "Please enter a stronger password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to check password strength
    private void checkPasswordStrength(String password) {
        if (password.length() < 8) {
            feedbackText.setText(R.string.password_is_too_short);
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (!password.matches(".*[A-Z].*")) {
            feedbackText.setText(R.string.password_must_contain_at_least_one_uppercase_letter);
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (!password.matches(".*[a-z].*")) {
            feedbackText.setText(R.string.password_must_contain_at_least_one_lowercase_letter);
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (!password.matches(".*\\d.*")) {
            feedbackText.setText(R.string.password_must_contain_at_least_one_digit);
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            feedbackText.setText(R.string.password_must_contain_at_least_one_special_symbol);
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            feedbackText.setText(R.string.strong_password);
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    // Check if password is strong based on feedback
    private boolean isPasswordStrong(String password) {
        return feedbackText.getText().toString().equals(getString(R.string.strong_password));
    }

    // Change password using Firebase
    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    auth.signOut(); // Sign out the user
                    Intent intent = new Intent(this, LoginActivity.class); // Redirect to login
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close the current activity
                } else {
                    Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
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
