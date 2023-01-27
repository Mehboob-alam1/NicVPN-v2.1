package com.nicadevelop.nicavpn.Ad_Module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.nicadevelop.nicavpn.R;

import java.util.Objects;

;

public class Ad extends AppCompatActivity {

    Intent intent;
    public static NativeAd nativeAd;
    NativeAdView adView;
    ImageView mSkipBtn;
    TextView mSkipTimerBtn;
    String destActivity = "";
    String sourceActivity = "";
    ConstraintLayout fb_adView;
    ConstraintLayout admob_native_layout, fb_native_layout;
    public static boolean isAlreadyClicked = false;
    private com.facebook.ads.NativeAdLayout nativeAdLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_layout);


        init_add();

        mSkipBtn.setOnClickListener(view -> {

            goNext();
            if (nativeAd != null) {
                nativeAd.destroy();
            }
            Cache_Adds.getInstance().init();
        });


        if (nativeAd != null) {

            populateAdView(nativeAd, adView);

        } else {
            goNext();
        }
    }

    private void init_add() {
        adView = findViewById(R.id.parent_ad_view);
        mSkipTimerBtn = findViewById(R.id.ad_skip_bt_timer);
        mSkipBtn = findViewById(R.id.ad_skip_bt);
        nativeAd = Cache_Adds.getInstance().getAd();
        setIntent();
    }


    @Override
    protected void onResume() {
        overridePendingTransition(0, 0);
        if (isAlreadyClicked) {
            overridePendingTransition(0, 0);
            goNext();
            isAlreadyClicked = false;
        }

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goNext();
    }


    private void populateAdView(NativeAd ad, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);

        //mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }

    public static int pxFromDp(final Context context, final float dp) {

        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }


    private void setMediaViewScale(MediaView mediaView) {

        mediaView.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
            }
        });
    }


    public void goNext() {

        try {
            if (destActivity != null && !destActivity.equals("")) {
                Intent newIntent = new Intent();

                if (intent.getParcelableExtra("SourceIntentData") != null)
                    newIntent = Objects.requireNonNull(intent.getExtras()).getParcelable("SourceIntentData");

                if (newIntent != null) {
                    newIntent.setClassName(getPackageName(), destActivity);
                    startActivity(newIntent);
                }
            }
        } catch (Exception e) {
            Log.d("goNext_Exception", "" + e.getMessage());
        }
        finish();
    }


    public void setIntent() {
        intent = getIntent();
        if (intent == null) return;
        sourceActivity = Objects.requireNonNull(intent.getExtras()).getString("Current_Activity");
        destActivity = Objects.requireNonNull(intent.getExtras()).getString("Destination_Activity");
    }
}



