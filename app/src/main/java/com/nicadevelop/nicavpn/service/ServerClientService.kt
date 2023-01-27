package com.nicadevelop.nicavpn.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nicadevelop.nicavpn.MainActivity
import com.nicadevelop.nicavpn.R
import com.nicadevelop.nicavpn.http.request.ClientServer
import com.nicadevelop.nicavpn.http.request.ServiceControl
import com.nicadevelop.nicavpn.http.request.listener.ByteCounter
import com.nicadevelop.nicavpn.http.request.listener.LogBox


class ServerClientService : Service(), ServiceControl {
    private var isOnForegroud = false
    private val controller = Controller()
    override var logBox: LogBox? = null
    private var server: ClientServer? = null
    private var counter: ByteCounter? = null
    private var notificationManager: NotificationManager? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    var CHANNEL_ID = "NOTIFY_CHANNEL_1"
    override fun onDestroy() {
        if (server != null) server!!.close()
        if (notificationManager != null) notificationManager!!.cancel(NOTINICATION_ID)
    }

    override fun onBind(intent: Intent): IBinder {
        return controller
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isOnForegroud) return START_NOT_STICKY
        isOnForegroud = true
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(this, MainActivity::class.java)
        PendingIntent.getActivity(this, 0, notificationIntent, 0)
        notificationBuilder =
            NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_service_running)
                .setContentTitle(getString(R.string.app_name_full))
                .setContentText(getString(R.string.notification_desc))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setOngoing(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "NdCheckMate"
            val description = "NdCheckmate"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationChannel.description = description
            notificationChannel.enableVibration(false)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        startForeground(NOTINICATION_ID, notificationBuilder!!.build())
        if (server != null) server!!.start()
        return START_NOT_STICKY
    }

    override fun setClientServer(
        serverAddr: String?, serverPort: Int, payloadConfig: String
    ) {
        server = object : ClientServer(serverAddr!!, serverPort, payloadConfig) {
            override fun onLog(log: String?) {
                if (notificationBuilder != null) {
                    notificationBuilder!!.setContentText(log)
                    notificationManager!!.notify(
                        NOTINICATION_ID, notificationBuilder!!.build()
                    )
                }
                if (logBox != null) {
                    logBox!!.addLog(log!!)
                }
            }
        }
        if (counter != null) {
            server!!.setByteCounter(counter)
        }
    }

    override val clientServer: ClientServer?
        get() = server


    override var byteCounter: ByteCounter?
        get() = counter
        set(counter) {
            this.counter = counter
            if (server != null) {
                server!!.setByteCounter(this.counter)
            }
        }

    inner class Controller : Binder() {
        val control: ServiceControl
            get() = this@ServerClientService
    }

    companion object {
        const val NOTINICATION_ID = 4242
    }
}