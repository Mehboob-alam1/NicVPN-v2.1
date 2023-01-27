package com.nicadevelop.nicavpn.Constant;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.nicadevelop.nicavpn.Ad_Module.Cache_Adds;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Constant {

    public static String GEO_API_URI = "http://ip-api.com/json/";

    //Admin Panel Link
    public static String MAIN_API = "http://v2-panel.jaquemate-vpn.net/";


    public static String Privacy_Policy = "https://unknowndev-vpn.tk/termsprivacy.html/";
    public static String SERVER_URL = MAIN_API + "api.php?action=get_serversobfs";


    public static String UPDATE_URL = MAIN_API + "api.php?action=get_update";
    public static String SETTINGS_URL = MAIN_API + "api.php?action=get_settings";

    public static final int REQUEST_CODE = 1;
    public static boolean IS_RUN = false;


    public static Boolean pay_done = false;
    //Real IDs

    public static String admob_app_id = "";
    public static String admob_banner = "";
    public static String admob_inter = "";
    public static String admob_native = "";
    public static String admob_reward = "";

    //Ads Switches

    public static Boolean banner_switch = true;
    public static Boolean inter_switch = true;
    public static Boolean native_switch = true;
    public static Boolean reward_switch = true;

    //About

    public static String Contact = "";
    public static String Email = "";
    public static String Website = "";
    public static String Company = "";


    //Update

    public static Boolean data_loaded = false;
    public static int Version;
    public static String Version_Name = "";
    public static String Link = "";
    public static String descrption = "";

    public static String millisToDateString(long millis, String formatString) {
        Date updatedate = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.US);
        return format.format(updatedate);
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public static boolean isOven_Vpn_ConnectionActive() {

        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return false;
    }
    public static void init_adds() {
        try {
            Cache_Adds.getInstance().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



