package com.rcm.eanimify;

import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class GlobalVariable {
    public static String first_name, last_name, email, password;
// Account Creation Variables
    public static EditText reg_first_name, reg_last_name, reg_email, reg_password;

    public static MaterialButton submit_btn;

//    Authentication
    public static FirebaseAuth mAuth;
}
