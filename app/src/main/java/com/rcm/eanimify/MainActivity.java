package com.rcm.eanimify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rcm.eanimify.Account.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    FirebaseFirestore firestore;

    FirebaseAuth auth;
    Button sign_out;
    TextView user_details;
    FirebaseUser user;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        sign_out = findViewById(R.id.sign_out);
        user_details = findViewById(R.id.user_details);
        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            db.collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String firstName = documentSnapshot.getString("firstName");
                                user_details.setText(firstName);
                            } else {
                                // Handle case where user data doesn't exist
                                user_details.setText(user.getEmail()); // Fallback to email
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                            user_details.setText(user.getEmail()); // Fallback to email
                        }
                    });
//            user_details.setText(user.getEmail());
        }

        if (user == null) {
            // User is not logged in, redirect to LoginActivity
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            if (user.isEmailVerified()) {
                // Email is verified, proceed as usual
                Toast.makeText(MainActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
            } else {
                // Email is not verified, show a message or redirect
                Toast.makeText(MainActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                // You can also choose to sign out the user and redirect to LoginActivity
                 FirebaseAuth.getInstance().signOut();
                 Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                 startActivity(intent);
                 finish();
            }
        }

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        firestore = FirebaseFirestore.getInstance();
//
//        Map<String, Object> users = new HashMap<>();
//        users.put("firstname", "John");
//        users.put("lastname", "Doe");
//        users.put("email", "john.mclean@examplepetstore.com");
//        users.put("password", "password");
//
//        firestore.collection("users").add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Toast.makeText(getApplicationContext(), "User added", Toast.LENGTH_SHORT).show();
//                }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "User not added", Toast.LENGTH_SHORT).show();
//            }
//        });

    }
}