package com.nicadevelop.nicavpn.About_Module;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nicadevelop.nicavpn.BuildConfig;
import com.nicadevelop.nicavpn.Constant.Constant;
import com.nicadevelop.nicavpn.R;
import com.codemybrainsout.ratingdialog.RatingDialog;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class About extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);




        Toolbar toolbarAbout = findViewById(R.id.toolbar_ab);
        setSupportActionBar(toolbarAbout);
        setTitle(getResources().getString(R.string.about));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbarAbout.setNavigationOnClickListener(view -> onBackPressed());


        TextView company = findViewById(R.id.company);
        TextView email = findViewById(R.id.email);
        TextView website = findViewById(R.id.website);
        TextView contact = findViewById(R.id.contact);


        company.setText(Constant.Company);
        email.setText(Constant.Email);
        website.setText(Constant.Website);
        contact.setText(Constant.Contact);

        LinearLayout btn_email = findViewById(R.id.btn_email);
        btn_email.setOnClickListener(v -> {
            Intent intent=new Intent(Intent.ACTION_SEND);
            String[] recipients={Constant.Email};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Feedback for "+getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, "You can send problems or suggestions to us." + "\n" +
                    "VersionName" + ":" + "  " + Build.VERSION.RELEASE + "\n" +
                    "VersionCode" + ":" + "  " + Build.MODEL + "\n" +
                    "Device Brand/Model: " + "  " + Build.MODEL + "\n" +
                    "System Version");
            intent.putExtra(Intent.EXTRA_CC,"");
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send mail"));

        });

        LinearLayout ll_share = findViewById(R.id.ll_share);
        ll_share.setOnClickListener(v -> {
            final String appName = getPackageName();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
        });

        LinearLayout ll_rate = findViewById(R.id.ll_rate);
        ll_rate.setOnClickListener(v -> rateUs());


        TextView version = findViewById(R.id.version);
        version.setText(BuildConfig.VERSION_NAME);
    }
    private void rateUs() {
        final RatingDialog ratingDialog;
        ratingDialog = new RatingDialog.Builder(this)


                .positiveButtonTextColor(R.color.colorPrimary)
                .feedbackTextColor(R.color.colorPrimary)
                .threshold(4)
                .ratingBarColor(R.color.colorPrimary)
                .onRatingBarFormSumbit(feedback -> {

                }).build();

        ratingDialog.show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}