package com.nicadevelop.nicavpn

import android.content.Intent
import android.os.Bundle
import androidx.preference.*
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.v2ray.ang.AppConfig
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.util.Utils
import com.v2ray.ang.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        title = getString(R.string.title_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        settingsViewModel.startListenPreferenceChange()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
    /*    private val localDns by lazy { findPreference(AppConfig.PREF_LOCAL_DNS_ENABLED) }



        private val concurrency by lazy { findPreference(AppConfig.PREF_CONCURRENCY) }
        private val fakeDns by lazy { findPreference(AppConfig.PREF_FAKE_DNS_ENABLED) }
        private val localDnsPort by lazy { findPreference(AppConfig.PREF_LOCAL_DNS_PORT) }
        private val vpnDns by lazy { findPreference(AppConfig.PREF_VPN_DNS) }
        private val sppedEnabled by lazy { findPreference(AppConfig.PREF_SPEED_ENABLED) as CheckBoxPreference }
        private val domainStrategy by lazy { findPreference(AppConfig.PREF_ROUTING_DOMAIN_STRATEGY) as ListPreference }
        private val routingMode by lazy { findPreference(AppConfig.PREF_ROUTING_MODE) as ListPreference }

        private val forwardIpv6 by lazy { findPreference(AppConfig.PREF_FORWARD_IPV6) as CheckBoxPreference }
        private val enableLocalDns by lazy { findPreference(AppConfig.PREF_LOCAL_DNS_ENABLED) as CheckBoxPreference }
        private val domesticDns by lazy { findPreference(AppConfig.PREF_DOMESTIC_DNS) as EditTextPreference }
        private val remoteDns by lazy { findPreference(AppConfig.PREF_REMOTE_DNS) as EditTextPreference }*/

        private fun restartProxy() {
            Utils.stopVService(requireContext())
            V2RayServiceManager.startV2Ray(requireContext())
        }

        private fun isRunning(): Boolean {
            return false //TODO no point of adding logic now since Settings will be changed soon
        }

        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            addPreferencesFromResource(R.xml.pref_settings)

       /*     sppedEnabled.setOnPreferenceClickListener {
                if (isRunning())
                    restartProxy()
                true
            }*/

          /*  domainStrategy.setOnPreferenceChangeListener { _, _ ->
                if (isRunning())
                    restartProxy()
                true
            }*/
          /*  routingMode.setOnPreferenceChangeListener { _, _ ->
                if (isRunning())
                    restartProxy()
                true
            }*/

         /*  forwardIpv6.setOnPreferenceClickListener {
                if (isRunning())
                    restartProxy()
                true
            }

            enableLocalDns.setOnPreferenceClickListener {
                if (isRunning())
                    restartProxy()
                true
            }


            domesticDns.setOnPreferenceChangeListener { _, any ->
                // domesticDns.summary = any as String
                val nval = any as String
                domesticDns.summary = if (nval == "") AppConfig.DNS_DIRECT else nval
                if (isRunning())
                    restartProxy()
                true
            }

            remoteDns.setOnPreferenceChangeListener { _, any ->
                // remoteDns.summary = any as String
                val nval = any as String
                remoteDns.summary = if (nval == "") AppConfig.DNS_AGENT else nval
                if (isRunning())
                    restartProxy()
                true
            }
            localDns?.setOnPreferenceChangeListener{ _, any ->
                updateLocalDns(any as Boolean)
                true
            }
            concurrency?.setOnPreferenceChangeListener{ _, any ->
                val nval = any as String
                concurrency?.summary = if (TextUtils.isEmpty(nval)) "6" else nval
                true
            }
            localDnsPort?.setOnPreferenceChangeListener { _, any ->
                val nval = any as String
                localDnsPort?.summary = if (TextUtils.isEmpty(nval)) "10807" else nval
                true
            }
            vpnDns?.setOnPreferenceChangeListener { _, any ->
                vpnDns?.summary = any as String
                true
            }*/
        }

        override fun onStart() {
            super.onStart()
         //   val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
          //  updateMode(defaultSharedPreferences.getString(AppConfig.PREF_MODE, "VPN"))
         //   var remoteDnsString = defaultSharedPreferences.getString(AppConfig.PREF_REMOTE_DNS, "")
          //  domesticDns.summary = defaultSharedPreferences.getString(AppConfig.PREF_DOMESTIC_DNS, "")

         //   if (TextUtils.isEmpty(remoteDnsString)) {
         //       remoteDnsString = AppConfig.DNS_AGENT
         //   }
         /*   if ( domesticDns.summary == "") {
                domesticDns.summary = AppConfig.DNS_DIRECT
            }
            remoteDns.summary = remoteDnsString
            vpnDns?.summary = defaultSharedPreferences.getString(AppConfig.PREF_VPN_DNS, remoteDnsString)*/
        }

        private fun updateMode(mode: String?) {
         //   val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val vpn = mode == "VPN"
         /*   localDns?.isEnabled = vpn
            fakeDns?.isEnabled = vpn
            localDnsPort?.isEnabled = vpn
            vpnDns?.isEnabled = vpn*/
            if (vpn) {
           //     updateLocalDns(defaultSharedPreferences.getBoolean(AppConfig.PREF_LOCAL_DNS_ENABLED, false))
            }
        }

        private fun updateLocalDns(enabled: Boolean) {
          //  fakeDns?.isEnabled = enabled
         //   localDnsPort?.isEnabled = enabled
         //   vpnDns?.isEnabled = !enabled
        }
    }
}