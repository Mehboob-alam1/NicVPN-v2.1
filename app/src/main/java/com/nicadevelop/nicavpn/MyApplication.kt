package com.nicadevelop.nicavpn


import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.nicadevelop.nicavpn.Application_Class.Application
import com.nicadevelop.nicavpn.Constant.Constant
import com.tencent.mmkv.MMKV
import com.v2ray.ang.AngApplication
import de.blinkt.openvpn.api.AppRestrictions
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.PRNGFixes
import de.blinkt.openvpn.core.StatusListener
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MyApplication : Application() {
    companion object {
        lateinit var appContext: Context
        const val PREF_LAST_VERSION = "pref_last_version"
    }

    private var requestQueue: RequestQueue? = null
    var curIndex = -1
    private val ONESIGNAL_APP_ID = "642a7689-4ee3-495d-9dd2-016c5b10eb12"
    var mInterstitialAd: InterstitialAd? = null

    override fun onCreate() {

        System.loadLibrary(Constants.LibUltima)
        appContext = applicationContext
        super.onCreate()

        // OpenVPN Start
        PRNGFixes.apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannels();

        val mStatus = StatusListener()
        mStatus.init(applicationContext)

        if (BuildConfig.DEBUG) enableStrictModes();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppRestrictions.getInstance(this).checkRestrictions(this);
        }

        val TAG = MyApplication::class.java.simpleName
        val defaultMap = HashMap<String, Any>()
        defaultMap["force_update"] = false
        defaultMap["force_update_debug"] = false
        defaultMap["new_version_on_playstore"] = BuildConfig.VERSION_CODE
        defaultMap["new_version_on_playstore_debug"] = BuildConfig.VERSION_CODE

        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        // firstRun = defaultSharedPreferences.getInt(PREF_LAST_VERSION, 0) != BuildConfig.VERSION_CODE
        //  if (firstRun)
        //     defaultSharedPreferences.edit().putInt(PREF_LAST_VERSION, BuildConfig.VERSION_CODE).apply()

        //Logger.init().logLevel(if (BuildConfig.DEBUG) LogLevel.FULL else LogLevel.NONE)
        MMKV.initialize(this)
    }


    private fun enableStrictModes() {
        val policy = VmPolicy.Builder().detectAll().penaltyLog().build()
        StrictMode.setVmPolicy(policy)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Background message
        var name: CharSequence = getString(R.string.channel_name_background)
        var mChannel = NotificationChannel(
            OpenVPNService.NOTIFICATION_CHANNEL_BG_ID, name, NotificationManager.IMPORTANCE_MIN
        )
        mChannel.description = getString(R.string.channel_description_background)
        mChannel.enableLights(false)
        mChannel.lightColor = Color.DKGRAY
        mNotificationManager.createNotificationChannel(mChannel)

        // Connection status change messages
        name = getString(R.string.channel_name_status)
        mChannel = NotificationChannel(
            OpenVPNService.NOTIFICATION_CHANNEL_NEWSTATUS_ID,
            name,
            NotificationManager.IMPORTANCE_LOW
        )
        mChannel.description = getString(R.string.channel_description_status)
        mChannel.enableLights(true)
        mChannel.lightColor = Color.BLUE
        mNotificationManager.createNotificationChannel(mChannel)

        // Urgent requests, e.g. two factor auth
        name = getString(R.string.channel_name_userreq)
        mChannel = NotificationChannel(
            OpenVPNService.NOTIFICATION_CHANNEL_USERREQ_ID,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        mChannel.description = getString(R.string.channel_description_userreq)
        mChannel.enableVibration(true)
        mChannel.lightColor = Color.CYAN
        mNotificationManager.createNotificationChannel(mChannel)

        // DNS Service

        // DNS Service
        name = "NicVPN"
        mChannel = NotificationChannel(
            "com.nicadevelop.nicavpn.service", name, NotificationManager.IMPORTANCE_HIGH
        )

        mChannel.description = "NicVPN"
        mChannel.enableVibration(true)
        mChannel.lightColor = Color.CYAN
        mNotificationManager.createNotificationChannel(mChannel)
    }


}