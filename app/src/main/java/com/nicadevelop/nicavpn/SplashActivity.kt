package com.nicadevelop.nicavpn

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.explorestack.consent.*
import com.explorestack.consent.exception.ConsentManagerException
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nicadevelop.nicavpn.Api_Fetch_Service.Fetch_Service
import com.nicadevelop.nicavpn.Api_Fetch_Service.api_data_model_updated
import com.nicadevelop.nicavpn.Constant.Constant.isNetworkAvailable
import com.nicadevelop.nicavpn.Constants.IS_RUN
import com.nicadevelop.nicavpn.System.loadLibrary
import io.michaelrocks.paranoid.Obfuscate
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

@Obfuscate
class SplashActivity : AppCompatActivity() {

    @Nullable
    private var consentForm: ConsentForm? = null
    var splash_sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLibrary(Constants.LibUltima)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        splash_sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE)

        setContentView(R.layout.activity_splash)
        resolveUserConsent()
    }

    private fun resolveUserConsent() {
        val consentManager = ConsentManager.getInstance(this)
        consentManager.requestConsentInfoUpdate(AppoDealAdManager.APP_KEY,
            object : ConsentInfoUpdateListener {
                override fun onConsentInfoUpdated(consent: Consent) {
                    val consentShouldShow: Consent.ShouldShow =
                        consentManager.shouldShowConsentDialog()
                    if (consentShouldShow === Consent.ShouldShow.TRUE) {
                        showConsentForm()
                    } else {
                        if (consent.status === Consent.Status.UNKNOWN) {
                            startMainActivity()
                        } else {
//                            val hasConsent = consent.status === Consent.Status.PERSONALIZED
                            startMainActivity()
                        }
                    }
                }

                override fun onFailedToUpdateConsentInfo(e: ConsentManagerException) {
                    startMainActivity()
                }
            })
    }

    private fun showConsentForm() {
        if (consentForm == null) {
            consentForm = ConsentForm.Builder(this).withListener(object : ConsentFormListener {
                    override fun onConsentFormLoaded() {
                        // Show ConsentManager Consent request form
                        consentForm!!.showAsActivity()
                    }

                    override fun onConsentFormError(error: ConsentManagerException) {
                        startMainActivity()
                    }

                    override fun onConsentFormOpened() {
                        //ignore
                    }

                    override fun onConsentFormClosed(consent: Consent) {
//                        val hasConsent = consent.status === Consent.Status.PERSONALIZED
                        startMainActivity()
                    }
                }).build()
        }
        if (consentForm!!.isLoaded) {
            consentForm!!.showAsActivity()
        } else {
            consentForm!!.load()
        }
    }

    private fun startMainActivity() {
        if (isNetworkAvailable(this@SplashActivity)) {
            if (!isOven_Vpn_ConnectionActive()) {
                start_service()
            } else {
                startMainActivity_move(true)
            }
        } else {
            if (getBestServer_after_calculation(splash_sharedPreferences) != null) {
                if (getBestServer_after_calculation(splash_sharedPreferences)!!.hostName.isNotEmpty()) {
                    startMainActivity_move(true)
                }
            } else {
                Toast.makeText(
                    this@SplashActivity,
                    "Unable to fetch data , check internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun start_service() {
        LocalBroadcastManager.getInstance(this@SplashActivity)
            .registerReceiver(data_fetch!!, IntentFilter("data_fetch_receiver"))
        try {
            val serviceIntent = Intent(this@SplashActivity, Fetch_Service::class.java)
            startService(serviceIntent)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }


    fun isOven_Vpn_ConnectionActive(): Boolean {
        var iface = ""
        try {
            for (networkInterface in Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp) iface = networkInterface.name
                Log.d("DEBUG", "IFACE NAME: $iface")
                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true
                }
            }
        } catch (e1: SocketException) {
            e1.printStackTrace()
        }
        return false
    }


    var data_fetch: BroadcastReceiver? = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val data_fetched = intent.getBooleanExtra("data_fetched", false)
            if (IS_RUN) {
                if (data_fetched) {
                    if (splash_sharedPreferences != null) {
                        val editor: SharedPreferences.Editor = splash_sharedPreferences!!.edit()
                        if (splash_sharedPreferences!!.contains("first_time_open")) {
                            editor.remove("first_time_open").apply()
                            editor.putBoolean("first_time_open", false).apply()
                        } else {
                            editor.putBoolean("first_time_open", true).apply()
                        }

                        startMainActivity_move(true)
                    } else {
                        //unable dialog
                        stopService(Intent(this@SplashActivity, Fetch_Service::class.java))
                        start_service()
                    }
                }
                IS_RUN = false
            }
        }
    }

    private fun startMainActivity_move(hasConsent: Boolean) {
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, IntroActivity::class.java)
            intent.putExtra("consent", hasConsent)
            startActivity(intent)
            finish()
        }, 1200)
    }


    fun getBestServer_after_calculation(sharedPreferences: SharedPreferences?): api_data_model_updated? {
        val responsePojo =
            getPreference("best_server_model", sharedPreferences) as api_data_model_updated?
        return if (responsePojo != null) {
            if (responsePojo.hostName != null) {
                responsePojo
            } else {
                null
            }
        } else null
    }

    fun getPreference(key: String?, global_sharedPreferences: SharedPreferences?): Any? {
        val gson = Gson()
        if (global_sharedPreferences != null) {
            val json_value = global_sharedPreferences.getString(key, null)

            if (json_value != null) {
                if (json_value.isNotEmpty()) {
                    try {
                        return gson.fromJson(json_value, api_data_model_updated::class.java)
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                } else {
                    return null
                }
            }
        }
        return null
    }
}

