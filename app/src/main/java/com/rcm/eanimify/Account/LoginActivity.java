package com.rcm.eanimify.Account;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rcm.eanimify.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.account_main);

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
        EditText firstname = (EditText) findViewById(R.id.firstname_TextField);
        EditText lastname = (EditText) findViewById(R.id.lastname_TextField);
        EditText email = (EditText) findViewById(R.id.email_TextField);
        EditText password = (EditText) findViewById(R.id.password_TextField);
    }
}