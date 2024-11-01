package com.rcm.eanimify.policy;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.rcm.eanimify.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy); // Create this layout

        WebView webView = findViewById(R.id.privacy_policy_webview); // Replace with your WebView ID
        webView.loadUrl("file:///android_asset/privacy_policy.html");    }

}
