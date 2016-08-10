package com.tgf.exhibition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl(getString(R.string.app_user_agreementation_url));
    }

}
