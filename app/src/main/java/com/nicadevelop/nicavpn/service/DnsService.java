package com.nicadevelop.nicavpn.service;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.nicadevelop.nicavpn.MainActivity;
import com.nicadevelop.nicavpn.MyApplication;
import com.nicadevelop.nicavpn.R;

import io.michaelrocks.paranoid.Obfuscate;
import me.lfasmpao.dnstunnel.Dnstt;
import me.lfasmpao.dnstunnel.FastDns;
import me.lfasmpao.dnstunnel.SlowDns;
import me.lfasmpao.dnstunnel.UdpMode;

@Obfuscate
public class DnsService extends Service {

    private FastDns fastDns;
    private SlowDns slowDns;
    private UdpMode udpMode;
    private Dnstt dnstt;

    private String host;
    private String extra;
    private String type_conn = "";
    private String dnstt_conn = "";
    private String dnstt_host = "";
    private String public_key = "";

    private final Object mProcessLock = new Object();

    private NotificationManager notificationManager;
    private static final String notificationChannel = "com.nicadevelop.nicavpn.service";
    private static final int notificationId = notificationChannel.hashCode();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }


    @Override
    public void onDestroy() {
        synchronized (mProcessLock) {
            if (type_conn.equals(MyApplication.appContext.getString(R.string.DNSTT_connection))) {
                dnstt.stop();
            }
            if (type_conn.equals(MyApplication.appContext.getString(R.string.fastdns_connection))) {
                fastDns.stop();
            }
            if (type_conn.equals(MyApplication.appContext.getString(R.string.UDP_connection))) {
                udpMode.stop();
            }
            // slowDns.stop();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(notificationId);
        } else {
            notificationManager.cancel(notificationId);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null) {
            host = intent.getExtras().getString("host"); // host name
            extra = intent.getExtras().getString("extra"); // extra args

            if (intent.hasExtra("type_conn")) {
                type_conn = intent.getExtras().getString("type_conn");
            }
            if (intent.hasExtra("dnstt_conn")) {
                dnstt_conn = intent.getExtras().getString("dnstt_conn");
            }
            if (intent.hasExtra("dnstt_host")) {
                dnstt_host = intent.getExtras().getString("dnstt_host");
            }
            if (intent.hasExtra("public_key")) {
                public_key = intent.getExtras().getString("public_key");
            }
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);


        PendingIntent pendingIntent = null;

        if (android.os.Build.VERSION.SDK_INT >= 31) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_MUTABLE);

        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        }


        Notification.Builder nbuilder = new Notification.Builder(this);
        nbuilder.setContentTitle("NicVPN");
        nbuilder.setContentText("NicVPN Service is running on background");
        nbuilder.setOnlyAlertOnce(true);
        nbuilder.setContentIntent(pendingIntent);
        nbuilder.setOngoing(true);

        nbuilder.setSmallIcon(R.drawable.appicon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nbuilder.setChannelId(notificationChannel);
        }

        @SuppressWarnings("deprecation") Notification notification = nbuilder.getNotification();

        startForeground(notificationId, notification);


        synchronized (mProcessLock) {
            if (type_conn.equals(MyApplication.appContext.getString(R.string.DNSTT_connection))) {
                dnstt = new Dnstt(getBaseContext(), getDnsttResolver(), dnstt_host, public_key, host, "127.0.0.1", "2323");
                dnstt.start();
            }
            if (type_conn.equals(MyApplication.appContext.getString(R.string.fastdns_connection))) {
                // for FastDNS
                fastDns = new FastDns(getBaseContext(), host, extra, "127.0.0.1", "2323");
                fastDns.start();
            }
            if (type_conn.equals(MyApplication.appContext.getString(R.string.UDP_connection))) {
                udpMode = new UdpMode(getBaseContext(), host, extra, "127.0.0.1", "2323");
                udpMode.start();
            }

            // for SlowDNS
            // slowDns = new SlowDns(mContext, host, "2323", "vpn", extra);
            // slowDns.start();
        }

        return Service.START_STICKY;
    }

    private Dnstt.Resolver getDnsttResolver() {
        if (dnstt_conn.equals("DOT")) {
            return Dnstt.Resolver.DOT;
        } else if (dnstt_conn.equals("DOH")) {
            return Dnstt.Resolver.DOH;
        }
        return Dnstt.Resolver.UDP;
    }
}
