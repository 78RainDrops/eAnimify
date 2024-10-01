package com.rcm.eanimify.Account;

import static com.rcm.eanimify.GlobalVariable.mAuth;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rcm.eanimify.GlobalVariable;
import com.rcm.eanimify.MainActivity;
import com.rcm.eanimify.R;

public class LoginActivity extends AppCompatActivity {

//    Account creation Variables
     EditText reg_first_name, reg_last_name, reg_email, reg_password;
     ProgressBar progressBar;
     TextView strong_password;

//     Account login Variables
    EditText login_email, login_password;

//    remember me variable
    CheckBox rememberMeCheckBox;
    EditText emailEditText;
    EditText passwordEditText;
    SharedPreferences preferences;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String savedEmail = preferences.getString("email", "");
        String savedPassword = preferences.getString("password", "");
        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            emailEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
            mAuth.signInWithEmailAndPassword(savedEmail, savedPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null && user.isEmailVerified()) {
                                    // Email is verified, proceed to MainActivity
                                    Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Email is not verified, show a message
                                    Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                                    // You can also choose to sign out the user here
                                    // FirebaseAuth.getInstance().signOut();
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.account_main);
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View loginForm = findViewById(R.id.login_form);
        View createAccountForm = findViewById(R.id.create_account_form);
        View loginButton = findViewById(R.id.login_form_button); // Make sure this ID exists in login_form
        View createAccountButton = findViewById(R.id.create_account_button); // Make sure this ID exists in create_account_form

        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginForm.setVisibility(View.VISIBLE);
                    createAccountForm.setVisibility(View.GONE);
                    if (login_email != null) {
                        login_email.setText("");
                    }
                    if (login_password != null) {
                        login_password.setText("");
                    }
                }
            });
        }

        if (createAccountButton != null) {
            createAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginForm.setVisibility(View.GONE);
                    createAccountForm.setVisibility(View.VISIBLE);
                    if (reg_first_name != null) {
                        reg_first_name.setText("");
                    }
                    if (reg_last_name != null) {
                        reg_last_name.setText("");
                    }
                    if (reg_email != null) {
                        reg_email.setText("");
                    }
                    if (reg_password != null) {
                        reg_password.setText("");
                    }
                }
            });
        }

//        registration
//        set up variables
         reg_first_name = findViewById(R.id.firstname_TextField);
         reg_last_name = findViewById(R.id.lastname_TextField);
         reg_email = findViewById(R.id.email_TextField);
         reg_password = findViewById(R.id.password_TextField);
         GlobalVariable.submit_btn = findViewById(R.id.submit_btn);
         progressBar = findViewById(R.id.progressBar);
         strong_password = findViewById(R.id.strong_password);

//      Strong password checker
        reg_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkPasswordStrength(charSequence.toString());
                strong_password.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkPasswordStrength(editable.toString());
                strong_password.setVisibility(View.VISIBLE);
            }
        });



//create an onlclick function for the submit button
         GlobalVariable.submit_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 progressBar.setVisibility(View.VISIBLE); //set the visibility of the progress bar to visible
//                 create a user instance, and add it to the database
                 String reg_firstname, reg_lastname, reg__email, reg__password;
                 reg_firstname = String.valueOf(reg_first_name.getText().toString());
                 reg_lastname = String.valueOf(reg_last_name.getText().toString());
                 reg__email = String.valueOf(reg_email.getText().toString());
                 reg__password = String.valueOf(reg_password.getText().toString());

                 if(TextUtils.isEmpty(reg_firstname)){
                     Toast.makeText(LoginActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    return;
                 }
                 if(TextUtils.isEmpty(reg_lastname)){
                     Toast.makeText(LoginActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if(TextUtils.isEmpty(reg__email)){
                     Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 if(TextUtils.isEmpty(reg__password)){
                     Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 mAuth.createUserWithEmailAndPassword(reg__email, reg__password)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                 progressBar.setVisibility(View.GONE);
                                 if (task.isSuccessful()) {
                                     // Sign in success, update UI with the signed-in user's information
                                     FirebaseUser user = mAuth.getCurrentUser();
                                     if (user != null) {
                                         // Create a new user instance
                                         User newUser = new User(reg_firstname, reg_lastname, reg__email);
                                         newUser.isVerified = false;//this will set the user as not verified
                                         // Add a new document with a generated ID
                                         db.collection("users")
                                                 .document(user.getUid()) // Use user's UID as document ID
                                                 .set(newUser)
                                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {
//                                                         If the account creation is successful it will create the account
//                                                         Toast.makeText(LoginActivity.this, "Account Created and data saved.",
//                                                                 Toast.LENGTH_SHORT).show();
                                                         Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                         startActivity(intent);
                                                         finish();
                                                     }
                                                 })
                                                 .addOnFailureListener(new OnFailureListener() {
                                                     @Override
                                                     public void onFailure(@NonNull Exception e) {
//                                                         if the account creation is not successful it will show an error message
                                                         Toast.makeText(LoginActivity.this, "Error saving user data.",
                                                                 Toast.LENGTH_SHORT).show();
                                                     }
                                                 });
                                     }
                                     if (user != null) {//if the user is not null or empty it will send a verification email
                                         user.sendEmailVerification()
                                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {
                                                         // Email sent successfully
                                                         Toast.makeText(LoginActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                                                         loginForm.setVisibility(View.VISIBLE);
                                                         createAccountForm.setVisibility(View.GONE);
                                                     }
                                                 })
                                                 .addOnFailureListener(new OnFailureListener() {
                                                     @Override
                                                     public void onFailure(@NonNull Exception e) {
                                                         // Handle errors
                                                         Toast.makeText(LoginActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                     }
                                                 });
                                     } else {
                                         // Handle the case where user is null (e.g., show an error message)
                                         Toast.makeText(LoginActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                                     }
                                 } else {
                                     // If sign in fails, display a message to the user.
                                     Toast.makeText(LoginActivity.this, "Authentication failed.",
                                             Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });
             }
         });
//Login
        login_email = findViewById(R.id.login_em);
        login_password = findViewById(R.id.login_pass);
        GlobalVariable.submit_btn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressBar);

        rememberMeCheckBox = findViewById(R.id.remember_me);
//        preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        GlobalVariable.submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String log_email, log_password;
                log_email = String.valueOf(login_email.getText().toString());
                log_password = String.valueOf(login_password.getText().toString());

                if(TextUtils.isEmpty(log_email)){
                    Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(log_password)){
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(log_email, log_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null && user.isEmailVerified()) {
                                        if (rememberMeCheckBox.isChecked()) {
                                            SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("email", log_email);
                                            editor.putString("password", log_password);
                                            editor.apply();
                                        }
                                        // Email is verified, proceed to MainActivity
                                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // Email is not verified, show a message
                                        Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                                        // You can also choose to sign out the user here
//                                         FirebaseAuth.getInstance().signOut();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

//Forgot password
        TextView forgotPasswordTextView = findViewById(R.id.forgot_password);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        if (mail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                            Toast.makeText(LoginActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                            return; // Stop execution if email is invalid
                        }
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // close the dialog
                    }
                });
                passwordResetDialog.create().show();
            }
        });

//        Show password
        EditText passwordEditText = findViewById(R.id.login_pass);
        EditText regPasswordEditText = findViewById(R.id.password_TextField);
        Drawable passwordIcon = passwordEditText.getCompoundDrawables()[2];

        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[2].getBounds().width())) {
                        togglePasswordVisibility(passwordEditText);
                        passwordEditText.performClick(); // Add this line
                        return true;
                    }
                }
                return false;
            }
        });

        passwordEditText.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action == AccessibilityNodeInfo.ACTION_CLICK) {
                    togglePasswordVisibility(passwordEditText);
                    return true;
                }
                return super.performAccessibilityAction(host, action, args);
            }
        });

//      registration
        regPasswordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (regPasswordEditText.getRight() - regPasswordEditText.getCompoundDrawables()[2].getBounds().width())) {
                        togglePasswordVisibility(regPasswordEditText);
                        regPasswordEditText.performClick();
                        return true;
                    }
                }
                return false;
            }
        });
        regPasswordEditText.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override
            public boolean performAccessibilityAction(View host, int action, Bundle args) {
                if (action == AccessibilityNodeInfo.ACTION_CLICK) {
                    togglePasswordVisibility(regPasswordEditText);
                    return true;
                }
                return super.performAccessibilityAction(host, action, args);
            }
        });

    }

    // Helper function to clear EditText fields within a ViewGroup
    private void clearEditTextFields(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof EditText) {
                ((EditText) child).setText("");
            } else if (child instanceof ViewGroup) {
                clearEditTextFields((ViewGroup) child); // Recursive call for nested ViewGroups
            }
        }
    }

//    @Override
//    public boolean performClick() {
//        super.performClick();
//        return true;
//    }

//Check password strength method
    private void checkPasswordStrength(String password) {
        TextView feedbackText = findViewById(R.id.strong_password);
        if (password.length() < 8) {
            feedbackText.setText(R.string.password_is_too_short);
        } else if (!password.matches(".*[A-Z].*")) {
            feedbackText.setText(R.string.password_must_contain_at_least_one_uppercase_letter);
        } else if (!password.matches(".*[a-z].*")) {
            feedbackText.setText(R.string.password_must_contain_at_least_one_lowercase_letter);
        } else if (!password.matches(".*\\d.*")) {
            feedbackText.setText(R.string.password_must_contain_at_least_one_digit);
        } else if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            feedbackText.setText(R.string.password_must_contain_at_least_one_special_symbol);
        } else {
            feedbackText.setText(R.string.strong_password);
        }
    }

//    Send reset password email
private void sendPasswordResetEmail(String email) {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}
//show passoword method
private void togglePasswordVisibility(EditText editText) {
    if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        // Change icon to open eye (if needed)
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_show_icon, 0);
    } else {
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        // Change icon to closed eye (if needed)
        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
    }
    editText.setSelection(editText.getText().length());
}
}