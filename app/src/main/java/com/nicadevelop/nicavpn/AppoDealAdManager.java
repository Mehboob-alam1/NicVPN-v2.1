package com.nicadevelop.nicavpn;

import android.app.Activity;
import android.util.Log;
import android.widget.RelativeLayout;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;

import io.michaelrocks.paranoid.Obfuscate;

@Obfuscate
public class AppoDealAdManager {

    private static AppoDealAdManager instance;


    private String TAG = "AppodealAdManager";

    public AppoDealAdManager() {
    }

    public static AppoDealAdManager Instance() {
        if (instance == null) {
            instance = new AppoDealAdManager();
        }
        return instance;
    }

    // Cambiar la variable a false cuando se vaya a pasar a produccion
    public static String APP_KEY = "ebbdf6f63c70addfd8ba03e856c071260ec58b7a34ce9a2f";


    public void initAppodeal(Activity context, boolean consent) {
        Appodeal.setTesting(BuildConfig.DEBUG);
        Appodeal.setLogLevel(com.appodeal.ads.utils.Log.LogLevel.debug);
        setAutoCache();
        Appodeal.initialize(context, APP_KEY, Appodeal.INTERSTITIAL, consent);
        Appodeal.initialize(context, APP_KEY, Appodeal.BANNER_VIEW, consent);
    }


    public void setAutoCache() {
        Appodeal.setAutoCache(Appodeal.BANNER_VIEW, true);
        Appodeal.setAutoCache(Appodeal.INTERSTITIAL, true);
    }

    public void loadAll(Activity context) {
        loadBanner(context);
        loadInterstitial(context);
    }

    public void loadInterstitial(Activity context) {
        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {

            }

            @Override
            public void onInterstitialFailedToLoad() {
                //       Toast.makeText(context.getApplicationContext(), "failed interestitial", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialShown() {

            }

            @Override
            public void onInterstitialShowFailed() {

            }

            @Override
            public void onInterstitialClicked() {

            }

            @Override
            public void onInterstitialClosed() {

            }

            @Override
            public void onInterstitialExpired() {

            }
        });
    }

    public void loadBanner(Activity context) {
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int i, boolean b) {
                Log.d(TAG, "onBannerLoaded: ");
            }

            @Override
            public void onBannerFailedToLoad() {
                Log.d(TAG, "onBannerFailedToLoad: ");
                Appodeal.setAutoCache(Appodeal.BANNER_VIEW, true);
            }

            @Override
            public void onBannerShown() {
                Log.d(TAG, "onBannerShown: ");
            }

            @Override
            public void onBannerShowFailed() {
                Log.d(TAG, "onBannerShowFailed: ");
            }

            @Override
            public void onBannerClicked() {
                Log.d(TAG, "onBannerClicked: ");
            }

            @Override
            public void onBannerExpired() {
                Log.d(TAG, "onBannerExpired: ");
            }
        });
    }

    public boolean isInterstitialLoaded() {
        return Appodeal.isLoaded(Appodeal.INTERSTITIAL);
    }

    public void showinterstitial(Activity context) {
        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean b) {

            }

            @Override
            public void onInterstitialFailedToLoad() {

            }

            @Override
            public void onInterstitialShown() {

            }

            @Override
            public void onInterstitialShowFailed() {

            }

            @Override
            public void onInterstitialClicked() {

            }

            @Override
            public void onInterstitialClosed() {

            }

            @Override
            public void onInterstitialExpired() {

            }
        });
        if (true) {
            Appodeal.show(context, Appodeal.INTERSTITIAL);
        }
    }


    public void showBannerAd(Activity context, RelativeLayout layout) {

        if (Appodeal.isLoaded(Appodeal.BANNER_VIEW)) {
            if (layout.getChildCount() > 0) {
                layout.removeAllViews();
            }
            layout.addView(Appodeal.getBannerView(context));

            Appodeal.show(context, Appodeal.BANNER_VIEW);
        } else {
            Appodeal.cache(context, Appodeal.BANNER_VIEW);
            Log.d(TAG, "showBannerAd: failed");
        }
    }

    public void showTopBanner(Activity context) {
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(context, Appodeal.BANNER_VIEW);
    }
}
