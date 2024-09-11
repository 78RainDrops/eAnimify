package com.rcm.eanimify.Account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rcm.eanimify.GlobalVariable;
import com.rcm.eanimify.MainActivity;
import com.rcm.eanimify.R;

public class LoginActivity extends AppCompatActivity {

//    Account creation Variables
     EditText reg_first_name, reg_last_name, reg_email, reg_password;
     ProgressBar progressBar;

//     Account login Variables
    EditText login_email, login_password;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = GlobalVariable.mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.account_main);
        GlobalVariable.mAuth = FirebaseAuth.getInstance();

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
                }
            });
        }

        if (createAccountButton != null) {
            createAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginForm.setVisibility(View.GONE);
                    createAccountForm.setVisibility(View.VISIBLE);
                }
            });
        }

//        registration
         reg_first_name = findViewById(R.id.firstname_TextField);
         reg_last_name = findViewById(R.id.lastname_TextField);
         reg_email = findViewById(R.id.email_TextField);
         reg_password = findViewById(R.id.password_TextField);
         GlobalVariable.submit_btn = findViewById(R.id.submit_btn);
         progressBar = findViewById(R.id.progressBar);

         GlobalVariable.submit_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 progressBar.setVisibility(View.VISIBLE);
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
//                 else{
//                     Toast.makeText(LoginActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
//                 }
                 GlobalVariable.mAuth.createUserWithEmailAndPassword(reg__email, reg__password)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                 progressBar.setVisibility(View.GONE);
                                 if (task.isSuccessful()) {
                                     // Sign in success, update UI with the signed-in user's information
                                     Toast.makeText(LoginActivity.this, "Account Created.",
                                             Toast.LENGTH_SHORT).show();
                                     Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                     startActivity(intent);
                                     finish();
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

                GlobalVariable.mAuth.signInWithEmailAndPassword(log_email, log_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(LoginActivity.this, "Login Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}