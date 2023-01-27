package com.nicadevelop.nicavpn;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.nicadevelop.nicavpn.About_Module.About;
import com.nicadevelop.nicavpn.Constant.Constant;
import com.nicadevelop.nicavpn.Premium_Feature.Premium_dialog;
import com.nicadevelop.nicavpn.R;


public class Menu_Activity extends AppCompatActivity {

    ImageView back_btn;
    TextView btn_myAccount, btn_upgradePlan, btn_tellFriends, btn_proxyApps, btn_contactUS, btn_aboutUs, btn_settings, btn_privacy;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        back_btn = findViewById(R.id.cross_icon);

        btn_myAccount = findViewById(R.id.btn_myAccount);
        btn_upgradePlan = findViewById(R.id.btn_upgradePlan);
        btn_tellFriends = findViewById(R.id.btn_tellFriends);
        btn_proxyApps = findViewById(R.id.btn_proxyApps);
        btn_contactUS = findViewById(R.id.btn_contactUS);
        btn_aboutUs = findViewById(R.id.btn_aboutUs);
        btn_settings = findViewById(R.id.btn_settings);
        btn_privacy = findViewById(R.id.btn_privacy);


        btn_settings.setVisibility(View.GONE);
        btn_proxyApps.setVisibility(View.GONE);


        back_btn.setOnClickListener(v -> onBackPressed());
        if (Constant.pay_done) {
            btn_upgradePlan.setVisibility(View.GONE);

        }


        btn_myAccount.setOnClickListener(v -> {
            startActivity(new Intent(Menu_Activity.this, AccountActivity.class));
        });

        btn_upgradePlan.setOnClickListener(v -> {
            Premium_dialog dialog = new Premium_dialog(Menu_Activity.this, R.style.AppTheme);
            dialog.show();
        });

        btn_tellFriends.setOnClickListener(v -> {


            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("cert_Value/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "NicVPN ");
                String shareMessage = "\nWe recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));

            } catch (Exception ignored) {
            }


        });

        btn_proxyApps.setOnClickListener(v -> {
            //  startActivity(new Intent(Menu_Activity.this, Proxy_Apps_Screen.class));
        });

        btn_contactUS.setOnClickListener(v -> {
            startActivity(new Intent(Menu_Activity.this, About.class));
        });

        btn_aboutUs.setOnClickListener(v -> {
            startActivity(new Intent(Menu_Activity.this, About.class));
        });

        btn_privacy.setOnClickListener(v -> {
            startActivity(new Intent(Menu_Activity.this, PrivacyPolicyActivity.class));
        });

        btn_settings.setOnClickListener(v -> {
            // startActivity(new Intent(Menu_Activity.this, Settings.class));
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}