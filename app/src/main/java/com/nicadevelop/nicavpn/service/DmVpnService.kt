package com.nicadevelop.nicavpn.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.nicadevelop.nicavpn.*
import com.nicadevelop.nicavpn.System
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsKeys
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmSharedPreferences
import com.nicadevelop.nicavpn.vpn.util.Routes
import com.nicadevelop.nicavpn.vpn.util.Utility
import java.util.*


class DmVpnService : VpnService() {
    internal inner class VpnBinder : IVpnService.Stub() {
        override fun isRunning(): Boolean {
            return mRunning
        }

        override fun stop() {
            stopMe()
        }
    }

    private var mInterface: ParcelFileDescriptor? = null
    private var mRunning = false
    private val mBinder = VpnBinder()
    private var mContext: Context? = null

    override fun onCreate() {
        super.onCreate()
        this.mContext = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "NdCheckMate",
                "NdCheckmate",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(false)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (BuildConfig.DEBUG) {
        }

        if (intent == null) {
            return Service.START_STICKY_COMPATIBILITY
        }

        if (mRunning) {
            return Service.START_STICKY_COMPATIBILITY
        }

        val name = intent.getStringExtra(Constants.INTENT_NAME)
        val server = intent.getStringExtra(Constants.INTENT_SERVER)
        val port = intent.getIntExtra(Constants.INTENT_PORT, 1080)
        val username = intent.getStringExtra(Constants.INTENT_USERNAME)
        val passwd = intent.getStringExtra(Constants.INTENT_PASSWORD)
        val route = intent.getStringExtra(Constants.INTENT_ROUTE)
        val dns = intent.getStringExtra(Constants.INTENT_DNS)
        val dnsPort = intent.getIntExtra(Constants.INTENT_DNS_PORT, 53)
        val perApp = intent.getBooleanExtra(Constants.INTENT_PER_APP, false)
        val appBypass = intent.getBooleanExtra(Constants.INTENT_APP_BYPASS, false)
        val appList = intent.getStringArrayExtra(Constants.INTENT_APP_LIST)
        val ipv6 = intent.getBooleanExtra(Constants.INTENT_IPV6_PROXY, false)
        val udpgw = intent.getStringExtra(Constants.INTENT_UDP_GW)

        // Create the notification
        startForeground(
            R.drawable.appicon,
            NotificationCompat.Builder(this, "NdCheckMate")
                .setContentTitle(getString(R.string.notify_title))
                .setContentText(String.format(getString(R.string.notify_msg), name))
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(android.R.color.transparent)
                .build()
        )

        // Create an fd.
        configure(name!!, route!!, perApp, appBypass, appList ?: arrayOf(), ipv6)

        if (mInterface != null) {
            if (BuildConfig.DEBUG) {
            }
            start(mInterface!!.fd, server!!, port, username, passwd, dns!!, dnsPort, ipv6, udpgw)
        }

        return Service.START_STICKY
    }

    override fun onRevoke() {
        super.onRevoke()
        stopMe()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()

        stopMe()
    }

    private fun stopMe() {
        stopForeground(true)

        Utility.killPidFile("$filesDir/tun2socks.pid")
        Utility.killPidFile("$filesDir/pdnsd.pid")

        try {
//            System.jniclose(mInterface!!.fd)
            mInterface!!.close()
        } catch (e: Exception) {
        }

        stopSelf()
    }

    private fun configure(
        name: String,
        route: String,
        perApp: Boolean,
        bypass: Boolean,
        apps: Array<String>,
        ipv6: Boolean
    ) {
        val b = Builder()
        b.setMtu(1500)
            .setSession(name)
            .addAddress("26.26.26.1", 24)
            .addDnsServer(
                DmSharedPreferences.getInstance(mContext!!)!!.getString(
                    DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS,
                    DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS_DEFAULT
                )!!
            )

        if (ipv6) {
            // Route all IPv6 traffic
            b.addAddress("fdfe:dcba:9876::1", 126)
                .addRoute("::", 0)
        }

        Routes.addRoutes(this, b, route)

        // Add the default DNS
        // Note that this DNS is just a stub.
        // Actual DNS requests will be redirected through pdnsd.
        b.addRoute(
            DmSharedPreferences.getInstance(mContext!!)!!.getString(
                DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS,
                DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS_DEFAULT
            )!!, 32
        )

        // Do app routing
        if (!perApp) {
            // Just bypass myself
            try {
                b.addDisallowedApplication("com.nicadevelop.nicavpn")
            } catch (e: Exception) {

            }
        } else {
            if (bypass) {
                // First, bypass myself
                try {
                    b.addDisallowedApplication("com.nicadevelop.nicavpn")
                } catch (e: Exception) {

                }

                for (p in apps) {
                    if (TextUtils.isEmpty(p))
                        continue

                    try {
                        b.addDisallowedApplication(p.trim { it <= ' ' })
                    } catch (e: Exception) {

                    }

                }
            } else {
                for (p in apps) {
                    if (TextUtils.isEmpty(p) || p.trim { it <= ' ' } == "com.nicadevelop.nicavpn") {
                        continue
                    }

                    try {
                        b.addAllowedApplication(p.trim { it <= ' ' })
                    } catch (e: Exception) {

                    }

                }
            }
        }

        mInterface = b.establish()
    }

    private fun start(
        fd: Int,
        server: String,
        port: Int,
        user: String?,
        passwd: String?,
        dns: String,
        dnsPort: Int,
        ipv6: Boolean,
        udpgw: String?
    ) {
        // Start DNS daemon first
        Utility.makePdnsdConf(this, dns, dnsPort)

        Utility.exec(
            String.format(
                Locale.US,
                "%s/libpdnsd.so -c %s/pdnsd.conf",
                applicationInfo.nativeLibraryDir,
                filesDir
            )
        )
        var command = String.format(
            Locale.US,
            "%s/libtun2socks.so --netif-ipaddr 26.26.26.2"
                    + " --netif-netmask 255.255.255.0"
                    + " --socks-server-addr %s:%d"
                    + " --tunfd %d"
                    + " --tunmtu 1500"
                    + " --loglevel 3"
                    + " --pid %s/tun2socks.pid"
                    + " --sock %s/sock_path",
            applicationInfo.nativeLibraryDir,
            server,
            port,
            fd,
            filesDir,
            applicationInfo.dataDir
        )
        if (user != null) {
            command += " --username $user"
        }
        if (passwd != null) {
            command += " --password $passwd"
        }

        if (ipv6) {
            command += " --netif-ip6addr fdfe:dcba:9876::2"
        }
        command += " --dnsgw 26.26.26.1:8091"
        if (udpgw != null) {
            command += " --udpgw-remote-server-addr $udpgw"
        }
        if (BuildConfig.DEBUG) {
        }
        if (Utility.exec(command) !== 0) {
            stopMe()
            return
        }

        // Try to send the Fd through socket.
        var i = 0
        while (i < 5) {
            if (System.sendfd(fd, applicationInfo.dataDir + "/sock_path") !== -1) {
                mRunning = true
                return
            }

            i++

            try {
                Thread.sleep((1000 * i).toLong())
            } catch (e: Exception) {

            }

        }

        // Should not get here. Must be a failure.
        stopMe()
    }

    companion object {

        private val TAG = DmVpnService::class.java.simpleName
    }
}
