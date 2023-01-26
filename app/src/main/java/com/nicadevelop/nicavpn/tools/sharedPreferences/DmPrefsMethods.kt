package com.nicadevelop.nicavpn.tools.sharedPreferences
import com.nicadevelop.nicavpn.MyApplication
import io.michaelrocks.paranoid.Obfuscate


@Obfuscate
object DmPrefsMethods {
    enum class DnsTtType(val value: String) {
        UDP("UDP"),
        DOT("DOT"),
        DOH("DOH")
    }

    fun getCustomPing(): Boolean {
        return DmSharedPreferences
            .getInstance(MyApplication.appContext)!!
            .getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING,
                DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING_DEFAULT
            )
    }

    fun setCustomPing(isCustomPing: Boolean = false) {
        DmSharedPreferences
            .getInstance(MyApplication.appContext)!!
            .putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING,
                isCustomPing
            )
    }

    fun setConnectionDnsTT(type: String) {
        DmSharedPreferences
            .getInstance(MyApplication.appContext)!!
            .putString(
                DmPrefsKeys.SHARED_PREFERENCES_DNS_TT_CONN,
                type
            )
    }

    fun getConnectionDnsTT(): String? {
        return DmSharedPreferences
            .getInstance(MyApplication.appContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_DNS_TT_CONN,
                DmPrefsKeys.SHARED_PREFERENCES_DNS_TT_CONN_DEFAULT
            )
    }

    fun setDnsTTHost(type: String) {
        DmSharedPreferences
            .getInstance(MyApplication.appContext)!!
            .putString(
                DmPrefsKeys.SHARED_PREFERENCES_DNS_TT,
                type
            )
    }

    fun getDnsTTHost(): String? {
        return DmSharedPreferences
            .getInstance(MyApplication.appContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_DNS_TT,
                DmPrefsKeys.SHARED_PREFERENCES_DNS_TT_DEFAULT
            )
    }
}