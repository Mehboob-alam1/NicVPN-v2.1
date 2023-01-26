package com.nicadevelop.nicavpn;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class AccountActivity extends AppCompatActivity {

    ImageView back_btn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_layout);
        back_btn = findViewById(R.id.back_icon);
        back_btn.setOnClickListener(v -> onBackPressed());


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}