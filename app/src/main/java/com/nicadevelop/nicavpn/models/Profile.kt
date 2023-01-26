package com.nicadevelop.nicavpn.models

import android.annotation.SuppressLint
import android.content.Context
import com.nicadevelop.nicavpn.Constants
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsKeys
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmSharedPreferences


@SuppressLint("ApplySharedPref")
class Profile internal constructor(private val mContext: Context, val name: String) {
    var server: String?
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_SERVER,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_SERVER_DEFAULT
            )
        set(server) {
            DmSharedPreferences.getInstance(mContext)!!
                .putString(DmPrefsKeys.SHARED_PREFERENCES_VPN_SERVER, server)
        }

    var port: Int
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getInt(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_SERVER_PORT,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_SERVER_PORT_DEFAULT
            )
        set(port) {
            DmSharedPreferences.getInstance(mContext)!!
                .putInt(DmPrefsKeys.SHARED_PREFERENCES_VPN_SERVER_PORT, port)
        }

    val isUserPw: Boolean
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_IS_VPN_AUTH,
                DmPrefsKeys.SHARED_PREFERENCES_IS_VPN_AUTH_DEFAULT
            )

    var username: String?
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_USERNAME,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_USERNAME_DEFAULT
            )
        set(username) {
            DmSharedPreferences.getInstance(mContext)!!
                .putString(DmPrefsKeys.SHARED_PREFERENCES_VPN_USERNAME, username)
        }

    var password: String?
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_PASSWORD,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_PASSWORD_DEFAULT
            )
        set(password) {
            DmSharedPreferences.getInstance(mContext)!!
                .putString(DmPrefsKeys.SHARED_PREFERENCES_VPN_PASSWORD, password)
        }

    var route: String?
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_ROUTE,
                Constants.ROUTE_ALL
            )
        set(route) {
            DmSharedPreferences.getInstance(mContext)!!
                .putString(DmPrefsKeys.SHARED_PREFERENCES_VPN_ROUTE, route)
        }

    var dns: String?
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS_DEFAULT
            )
        set(dns) {
            DmSharedPreferences.getInstance(mContext)!!
                .putString(DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS, dns)
        }

    var dnsPort: Int
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getInt(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS_PORT,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS_PORT_DEFAULT
            )
        set(port) {
            DmSharedPreferences.getInstance(mContext)!!
                .putInt(DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS_PORT, port)
        }

    var isPerApp: Boolean
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_PER_APP,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_PER_APP_DEFAULT
            )
        set(`is`) {
            DmSharedPreferences.getInstance(mContext)!!
                .putBoolean(DmPrefsKeys.SHARED_PREFERENCES_VPN_PER_APP, `is`)
        }

    var isBypassApp: Boolean
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_BYPASS_APP,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_BYPASS_APP_DEFAULT
            )
        set(`is`) {
            DmSharedPreferences.getInstance(mContext)!!
                .putBoolean(DmPrefsKeys.SHARED_PREFERENCES_VPN_BYPASS_APP, `is`)
        }

    var appList: String?
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_APP_LIST,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_APP_LIST_DEFAULT
            )
        set(list) {
            DmSharedPreferences.getInstance(mContext)!!
                .putString(DmPrefsKeys.SHARED_PREFERENCES_VPN_APP_LIST, list)
        }

    var udpgw: String?
        get() = DmSharedPreferences.getInstance(mContext)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_UDPGW,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_UDPGW_DEFAULT
            )
        set(gw) {
            DmSharedPreferences.getInstance(mContext)!!
                .putString(DmPrefsKeys.SHARED_PREFERENCES_VPN_UDPGW, gw)
        }

    fun setIsUserpw(`is`: Boolean) {
        DmSharedPreferences.getInstance(mContext)!!
            .putBoolean(DmPrefsKeys.SHARED_PREFERENCES_IS_VPN_AUTH, `is`)
    }

    fun hasIPv6(): Boolean {
        return DmSharedPreferences.getInstance(mContext)!!
            .getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_IPV6,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_IPV6_DEFAULT
            )
    }

    fun setHasIPv6(has: Boolean) {
        DmSharedPreferences.getInstance(mContext)!!
            .putBoolean(DmPrefsKeys.SHARED_PREFERENCES_VPN_IPV6, has)
    }

    fun hasUDP(): Boolean {
        return DmSharedPreferences.getInstance(mContext)!!
            .getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_UDP,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_UDP_DEFAULT
            )
    }

    fun setHasUDP(has: Boolean) {
        DmSharedPreferences.getInstance(mContext)!!
            .putBoolean(DmPrefsKeys.SHARED_PREFERENCES_VPN_UDP, has)
    }

    fun autoConnect(): Boolean {
        return DmSharedPreferences.getInstance(mContext)!!
            .getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_CONNECT_ON_BOOT,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_CONNECT_ON_BOOT_DEFAULT
            )
    }

    fun setAutoConnect(auto: Boolean) {
        DmSharedPreferences.getInstance(mContext)!!
            .putBoolean(DmPrefsKeys.SHARED_PREFERENCES_VPN_CONNECT_ON_BOOT, auto)
    }

    internal fun delete() {
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_SERVER)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_SERVER_PORT)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_IS_VPN_AUTH)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_USERNAME)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_PASSWORD)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_ROUTE)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS_PORT)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_PER_APP)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_BYPASS_APP)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_APP_LIST)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_IPV6)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_UDP)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_UDPGW)
        DmSharedPreferences.getInstance(mContext)!!.remove(DmPrefsKeys.SHARED_PREFERENCES_VPN_CONNECT_ON_BOOT)
    }
}
