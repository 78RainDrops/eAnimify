package com.rcm.eanimify.Account;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rcm.eanimify.GlobalVariable;
import com.rcm.eanimify.R;

public class LoginActivity extends AppCompatActivity {

//    Account creation Variables
//    EditText reg_firstname, reg_lastname, reg_email, reg_password;
//    MaterialButton submit_btn;

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
         GlobalVariable.reg_first_name = findViewById(R.id.firstname_TextField);
         GlobalVariable.reg_last_name = findViewById(R.id.lastname_TextField);
         GlobalVariable.reg_email = findViewById(R.id.email_TextField);
         GlobalVariable.reg_password = findViewById(R.id.password_TextField);
         GlobalVariable.submit_btn = findViewById(R.id.submit_btn);

         GlobalVariable.submit_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String reg_first_name, reg_last_name, reg_email, reg_password;
                 reg_first_name = String.valueOf(GlobalVariable.reg_first_name.getText().toString());
                 reg_last_name = String.valueOf(GlobalVariable.reg_last_name.getText().toString());
                 reg_email = String.valueOf(GlobalVariable.reg_email.getText().toString());
                 reg_password = String.valueOf(GlobalVariable.reg_password.getText().toString());

                 if(TextUtils.isEmpty(reg_first_name)){
                     Toast.makeText(LoginActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                 }
                 if(TextUtils.isEmpty(reg_last_name)){
                     Toast.makeText(LoginActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                 }
                 if(TextUtils.isEmpty(reg_email)){
                     Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                 }
                 if(TextUtils.isEmpty(reg_password)){
                     Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                 }
//                 else{
//                     Toast.makeText(LoginActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
//                 }
                 GlobalVariable.mAuth.createUserWithEmailAndPassword(reg_email, reg_password)
                         .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                             @Override
                             public void onComplete(@NonNull Task<AuthResult> task) {
                                 if (task.isSuccessful()) {
                                     // Sign in success, update UI with the signed-in user's information
                                     FirebaseUser user = GlobalVariable.mAuth.getCurrentUser();
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