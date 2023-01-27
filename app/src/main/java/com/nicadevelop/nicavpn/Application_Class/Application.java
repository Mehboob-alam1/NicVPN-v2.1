package com.nicadevelop.nicavpn.Application_Class;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.nicadevelop.nicavpn.Constant.Constant;
import com.nicadevelop.nicavpn.MyApplication;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Application extends MultiDexApplication {

    private static final String ONESIGNAL_APP_ID = "642a7689-4ee3-495d-9dd2-016c5b10eb12";
    public static InterstitialAd mInterstitialAd;
    private RequestQueue requestQueue;
    public static RewardedAd mRewardedAd;


    @Override
    public void onCreate() {
        super.onCreate();
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);


        Get_Settings();
        Get_Update();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, status.getDescription(), status.getLatency()));
                }

                // Start loading ads here...


            }
        });

    }


    private void Get_Settings() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.SETTINGS_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {


                    Constant.Privacy_Policy = response.getString("app_privacy_policy");


                    Constant.Contact = response.getString("contact");
                    Constant.Email = response.getString("email");
                    Constant.Website = response.getString("website");
                    Constant.Company = response.getString("company");


                    Constant.banner_switch = response.getBoolean("banner_ad");
                    Constant.inter_switch = response.getBoolean("interstital_ad");
                    Constant.native_switch = response.getBoolean("admob_nathive_ad");
                    Constant.reward_switch = response.getBoolean("reward_ad");

                    Constant.admob_app_id = response.getString("publisher_id");
                    Constant.admob_banner = response.getString("banner_ad_id");
                    Constant.admob_inter = response.getString("interstital_ad_id");
                    Constant.admob_native = response.getString("admob_native_ad_id");
                    Constant.admob_native = response.getString("admob_native_ad_id");
                    Constant.admob_reward = response.getString("reward_id");


                    load_reward_ad();
                    load_rinterstitial_Ad();
                    Constant.init_adds();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Exception", error.toString());

            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private void Get_Update() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constant.UPDATE_URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Constant.Version = response.getInt("version");
                    Constant.Version_Name = response.getString("version_name");
                    Constant.Link = response.getString("url");
                    Constant.descrption = response.getString("description");


                    Constant.data_loaded = true;


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Exception", error.toString());
            }
        });

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }


    public static void load_rinterstitial_Ad() {

        if (!Constant.pay_done) {
            if (Constant.inter_switch) {
                AdRequest adRequest = new AdRequest.Builder().build();

                InterstitialAd.load(MyApplication.appContext, Constant.admob_inter, adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        //  Log.i(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                load_rinterstitial_Ad();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        //   Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });


            }

        }

    }


    public static void load_reward_ad() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(MyApplication.appContext, "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error.
                //     Log.d(TAG, loadAdError.toString());
                mRewardedAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
                // Log.d(TAG, "Ad was loaded.");


                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        // Called when a click is recorded for an ad.
                        //     Log.d(TAG, "Ad was clicked.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Set the ad reference to null so you don't show the ad a second time.
                        //       Log.d(TAG, "Ad dismissed fullscreen content.");
                        mRewardedAd = null;
                        load_reward_ad();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        //     Log.e(TAG, "Ad failed to show fullscreen content.");
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        //    Log.d(TAG, "Ad recorded an impression.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        //    Log.d(TAG, "Ad showed fullscreen content.");
                    }
                });


            }
        });
    }

}