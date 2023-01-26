package com.nicadevelop.nicavpn.Ad_Module;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.nicadevelop.nicavpn.Constant.Constant;
import com.nicadevelop.nicavpn.MyApplication;

import java.util.ArrayList;


public class Cache_Adds {

    private static Cache_Adds instance;

    private final String adUnitId = Constant.admob_native;
    private ArrayList<NativeAd> nativeAd = new ArrayList<>();

    public static Cache_Adds getInstance() {
        if (instance == null)
            instance = new Cache_Adds();
        return instance;
    }

    public void init() {
        loadAd(null);
    }

    private void loadAd(NativeAd.OnNativeAdLoadedListener listener) {

        if(!Constant.pay_done) {
            if (Constant.native_switch){
                NativeAd.OnNativeAdLoadedListener adLoadedListener =
                        unifiedNativeAd -> {
                            if (listener != null){
                                listener.onNativeAdLoaded(unifiedNativeAd);
                            } else {
                                nativeAd.add(unifiedNativeAd);
                            }
                        };

                AdListener adListener = new AdListener() {



                    @Override
                    public void onAdClicked() {
                        // Log.d(TAG, "onAdClicked: ");
                        super.onAdClicked();
                    }
                };

                AdLoader adLoader;
                adLoader = new AdLoader.Builder(MyApplication.appContext, adUnitId)
                        .forNativeAd(adLoadedListener)
                        .withAdListener(adListener)
                        .withNativeAdOptions(new NativeAdOptions.Builder().build())
                        .build();
                adLoader.loadAd(new AdRequest.Builder().build());
            }

    }
    }

    public NativeAd getAd() {

        if (nativeAd.size() > 0)
            return nativeAd.remove(0);
        else {
            //loadAd(null);
            return null;
        }
    }

    public Boolean containsAd() {
        return nativeAd != null;
    }


}
