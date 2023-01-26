package com.nicadevelop.nicavpn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.nicadevelop.nicavpn.Constant.Constant;
import com.nicadevelop.nicavpn.R;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class PrivacyPolicyActivity extends AppCompatActivity {

    private Toolbar toolbarPolicy;
    private ProgressBar progressBar_policy;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);



        toolbarPolicy = findViewById(R.id.toolbarpolicy);
        setSupportActionBar(toolbarPolicy);
        setTitle(getResources().getString(R.string.privacy_policy));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbarPolicy.setNavigationOnClickListener(view -> onBackPressed());



        progressBar_policy = findViewById(R.id.progressBar_policy);
        WebView privacy_policy = findViewById(R.id.privacy_policy);
        privacy_policy.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                toolbarPolicy.setTitle("Loading...");
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if(progress == 100){
                    toolbarPolicy.setTitle(R.string.privacy_policy);
                    progressBar_policy.setVisibility(View.GONE);
                }
            }
        });
        privacy_policy.getSettings().setJavaScriptEnabled(true);
        privacy_policy.loadUrl(Constant.MAIN_API+"privacy_policy.php");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}