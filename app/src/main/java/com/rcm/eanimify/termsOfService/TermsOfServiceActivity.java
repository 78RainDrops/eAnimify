package com.rcm.eanimify.termsOfService;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rcm.eanimify.R;

public class TermsOfServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terms_of_service);

        WebView webView = findViewById(R.id.terms_content);
        webView.loadUrl("file:///android_asset/terms_of_service.html");
    }

}