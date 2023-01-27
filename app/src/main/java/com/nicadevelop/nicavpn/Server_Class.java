package com.nicadevelop.nicavpn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nicadevelop.nicavpn.Api_Fetch_Service.Api;
import com.nicadevelop.nicavpn.Api_Fetch_Service.Fetch_Service;
import com.nicadevelop.nicavpn.Api_Fetch_Service.api_data_model_updated;
import com.nicadevelop.nicavpn.Api_Fetch_Service.api_response;
import com.nicadevelop.nicavpn.Application_Class.Application;
import com.nicadevelop.nicavpn.Constant.Constant;
import com.nicadevelop.nicavpn.adapters.ServerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.nicadevelop.nicavpn.Constant.Constant.isNetworkAvailable;
import static com.nicadevelop.nicavpn.Constant.Constant.isOven_Vpn_ConnectionActive;


public class Server_Class extends AppCompatActivity {

    Activity contexts;
    ImageView toolbar_back_button, refresh_icon;
    ServerAdapter serverAdapter;
    ArrayList<api_response> api_responses;
    close_dialog closeDialog;
    RecyclerView server_recycler;
    SharedPreferences Server_choose_preference;
    api_response api_model_server_list;
    Dialog dialog_home_server_list;
    ProgressBar progress_bar_server_list;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_window);
        if (Application.mInterstitialAd != null) {
            Application.mInterstitialAd.show(Server_Class.this);
        } else {
            Application.load_rinterstitial_Ad();
        }
        Server_choose_preference = getSharedPreferences("DATA", MODE_PRIVATE);

        api_responses = new ArrayList<>();
        toolbar_back_button = findViewById(R.id.toolbar_back_button);
        refresh_icon = findViewById(R.id.refresh_icon);

        refresh_icon.setOnClickListener(view -> {
            if (isNetworkAvailable(Server_Class.this)) {
                if (!isOven_Vpn_ConnectionActive()) {
                    get_all_apis();
                } else {
                    Toast.makeText(Server_Class.this, "Disconnect vpn first..!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Server_Class.this, "Unable to fetch data , check internet connection", Toast.LENGTH_LONG).show();
            }

        });


        toolbar_back_button.setOnClickListener(v -> {
            onBackPressed();
        });


        server_recycler = findViewById(R.id.server_recycler);

        populate_data();
    }

    private void get_all_apis() {

        dialog_home_server_list = null;
        dialog_home_server_list = new Dialog(Server_Class.this);
        dialog_home_server_list.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_home_server_list.setContentView(R.layout.loading_progress_layout);
        progress_bar_server_list = dialog_home_server_list.findViewById(R.id.imageDialog);
        progress_bar_server_list.setVisibility(View.VISIBLE);
        dialog_home_server_list.setCancelable(false);

        if (!(Server_Class.this.isFinishing())) {
            runOnUiThread(() -> dialog_home_server_list.show());
        }

        hit_api();
    }


    private void hit_api() {

        RequestQueue queue = Volley.newRequestQueue(Server_Class.this);
        StringRequest stringRequest = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            stringRequest = new StringRequest(Request.Method.GET, Constant.SERVER_URL, this::get_pre_static_data, error -> {
            });
        }

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 2));
        queue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void get_pre_static_data(String input) {
        parse_data(input);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void parse_data(String input) {
        if (api_responses == null) api_responses = new ArrayList<>();

        api_responses.clear();
        try {
            JSONObject jsonObject = new JSONObject(input);
            JSONArray jsonArray = jsonObject.getJSONArray("servers");

            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    try {
//                            object.getInt("time")
                        api_model_server_list = new api_response();
                        api_model_server_list.setServer_id(object.getInt("server_id"));
                        api_model_server_list.setServerStatus(object.getInt("ServerStatus"));
                        api_model_server_list.setHostName(object.getString("HostName"));
                        api_model_server_list.setCity(object.getString("city"));
                        api_model_server_list.setIP(object.getString("IP"));
                        api_model_server_list.set_type(object.getInt("type"));
                        api_model_server_list.setTimer_val(object.getInt("time"));
                        api_model_server_list.setV2ray_udp(object.getString("v2ray_udp"));
                        api_model_server_list.setV2ray_tcp(object.getString("v2ray_tcp"));
                        api_model_server_list.setPublickey(object.getString("publickey"));
                        api_model_server_list.setDrawable(object.getString("drawable"));
                        api_model_server_list.setIp_dnstt(object.getString("ip_dnstt"));

                        api_responses.add(api_model_server_list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (api_responses != null && !api_responses.isEmpty()) {
                    Gson gson = new Gson();
                    String json = gson.toJson(api_responses);
                    if (!json.isEmpty()) {
                        SharedPreferences.Editor editor = Server_choose_preference.edit();
                        if (isJSONValid(json)) {
                            if (Server_choose_preference.contains("list_saved_cache")) {
                                editor.remove("list_saved_cache").apply();
                            }
                            ;

                            editor.putString("list_saved_cache", json).apply();

                            if (dialog_home_server_list != null && dialog_home_server_list.isShowing()) {
                                dialog_home_server_list.dismiss();
                                progress_bar_server_list.setVisibility(View.INVISIBLE);
                            }

                            populate_data();
                        }
                    }
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    public ArrayList<api_response> getAllServers_from_cache() {

        try {
            Gson gson = new Gson();
            if (Server_choose_preference != null) {
                String json = Server_choose_preference.getString("list_saved_cache", null);
                if (json != null) {
                    if (!json.isEmpty() || !json.equals("")) {
                        return gson.fromJson(json, Api.class);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private void populate_data() {
        api_responses.clear();
        api_responses = getAllServers_from_cache();

        if (!api_responses.isEmpty()) {
            serverAdapter = new ServerAdapter(Server_Class.this, api_responses);
            server_recycler.setLayoutManager(new LinearLayoutManager(Server_Class.this));
            ViewCompat.setNestedScrollingEnabled(server_recycler, false);
            server_recycler.setAdapter(serverAdapter);
            serverAdapter.notifyDataSetChanged();
        }
    }
}

