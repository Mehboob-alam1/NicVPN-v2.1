package com.nicadevelop.nicavpn.vpn.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.nicadevelop.nicavpn.Constants
import com.nicadevelop.nicavpn.R
import com.nicadevelop.nicavpn.models.Profile
import com.nicadevelop.nicavpn.service.DmVpnService
import com.nicadevelop.nicavpn.tools.LogUtil
import io.michaelrocks.paranoid.Obfuscate

import java.io.*

@Obfuscate
object Utility {
    private const val TAG = "Utility"
    fun exec(cmd: String?): Int {
        return try {
            val p = Runtime.getRuntime().exec(cmd)
            p.waitFor()
        } catch (e: Exception) {
            -1
        }
    }

    fun killPidFile(f: String?) {
        val file = File(f!!)
        if (!file.exists()) {
            return
        }
        val i: InputStream
        i = try {
            FileInputStream(file)
        } catch (e: Exception) {
            return
        }
        val buf = ByteArray(512)
        var len: Int
        val str = StringBuilder()
        try {
            while (i.read(buf, 0, 512).also { len = it } > 0) {
                str.append(String(buf, 0, len))
            }
            i.close()
        } catch (e: Exception) {
            return
        }
        try {
            val pid = str.toString().trim { it <= ' ' }.replace("\n", "").toInt()
            Runtime.getRuntime().exec("kill $pid").waitFor()
            if (!file.delete()) LogUtil.w(LogUtil.DIAM_PROXY_TAG, "$TAG failed to delete pidfile")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun join(
        list: List<String?>,
        separator: String
    ): String {
        if (list.isEmpty()) return ""
        val ret = StringBuilder()
        for (s in list) {
            ret.append(s).append(separator)
        }
        return ret.substring(0, ret.length - separator.length)
    }

    fun makePdnsdConf(
        context: Context,
        dns: String?,
        port: Int
    ) {
        val conf: String = context.getString(R.string.pdnsd_conf)
            .replace("{DIR}", context.filesDir.toString())
            .replace("{IP}", dns!!)
            .replace("{PORT}", port.toString())
        val f = File(context.filesDir.toString() + "/pdnsd.conf")
        if (f.exists()) {
            if (!f.delete()) Log.w(TAG, "failed to delete pdnsd.conf")
        }
        try {
            val out: OutputStream = FileOutputStream(f)
            out.write(conf.toByteArray())
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val cache = File(context.filesDir.toString() + "/pdnsd.cache")
        if (!cache.exists()) {
            try {
                if (!cache.createNewFile()) Log.w(
                    TAG,
                    "failed to create pdnsd.cache"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startVpn(context: Context, profile: Profile) {
        val i = Intent(context, DmVpnService::class.java)
            .putExtra(Constants.INTENT_NAME, profile.name)
            .putExtra(Constants.INTENT_SERVER, profile.server)
            .putExtra(Constants.INTENT_PORT, profile.port)
            .putExtra(Constants.INTENT_ROUTE, profile.route)
            .putExtra(Constants.INTENT_DNS, profile.dns)
            .putExtra(Constants.INTENT_DNS_PORT, profile.dnsPort)
            .putExtra(Constants.INTENT_PER_APP, profile.isPerApp)
            .putExtra(Constants.INTENT_IPV6_PROXY, profile.hasIPv6())

        if (profile.isUserPw) {
            i.putExtra(Constants.INTENT_USERNAME, profile.username)
                .putExtra(Constants.INTENT_PASSWORD, profile.password)
        }

        if (profile.isPerApp) {
            i.putExtra(Constants.INTENT_APP_BYPASS, profile.isBypassApp)
                .putExtra(Constants.INTENT_APP_LIST,
                    profile.appList!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                )
        }

        if (profile.hasUDP()) {
            i.putExtra(Constants.INTENT_UDP_GW, profile.udpgw)
        }

        context.startService(i)
    }
}
