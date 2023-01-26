package com.nicadevelop.nicavpn.service

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nicadevelop.nicavpn.R.drawable
import com.nicadevelop.nicavpn.R.string
import com.nicadevelop.nicavpn.MainActivity


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */

class HTTPtunnelIntentService : IntentService("StunnelIntentService") {
    private var currLogValue = ""
    private val processManager = HTTProcessManager()
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action: String = intent.action!!
            if (ACTION_STARTNOVPN == action) {
                handleStart()
            }
        }
    }

    /**
     * Handle start action in the provided background thread with the provided
     * parameters.
     */
    private fun handleStart() {
        privateIsRunning.postValue(true)
        showNotification()
        processManager.start(this)
    }

    override fun onDestroy() {
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "StunnelIntentService.onDestroy")
        processManager.stop(this)
        removeNotification()
        privateIsRunning.postValue(false)
        super.onDestroy()
    }

    fun appendLog(value: String) {
        currLogValue += value + "\n"
        logDataPrivate.postValue(currLogValue)
    }

    fun clearLog() {
        currLogValue = ""
        logDataPrivate.postValue(currLogValue)
    }

    private fun showNotification() {
        val mBuilder: Builder = Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(drawable.ic_service_running)
                .setContentTitle(getString(string.app_name_full))
                .setContentText(getString(string.notification_desc))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
        // Creates an explicit intent for an Activity in your app


        val resultIntent = Intent(this, MainActivity::class.java)
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.


        val stackBuilder = TaskStackBuilder.create(this)
        // Adds the back stack for the Intent (but not the Intent itself)


        stackBuilder.addParentStack(MainActivity::class.java)
        // Adds the Intent that starts the Activity to the top of the stack


        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val serviceStopIntent = Intent(this, ServiceStopReceiver::class.java)
        serviceStopIntent.action = ACTION_STOP
        val serviceStopIntentPending: PendingIntent? = PendingIntent.getBroadcast(this, 1, serviceStopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        //mBuilder.addAction(drawable.ic_stop, "Stop", serviceStopIntentPending)

        // Ensure that the service is a foreground service


        startForeground(NOTIFICATION_ID, mBuilder.build())
    }

    private fun removeNotification() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager?.cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val ACTION_STARTNOVPN = "tcp.nicadevelop.ndcheckmate.service.action.STARTNOVPN"
        private const val ACTION_RESUMEACTIVITY = "tcp.nicadevelop.ndcheckmate.service.action.RESUMEACTIVITY"
        private const val NOTIFICATION_ID = 1
        const val ACTION_STOP = "tcp.nicadevelop.ndcheckmate.service.action.STOP"
        private val privateIsRunning = MutableLiveData<Boolean>()
        val isRunning: LiveData<Boolean> = privateIsRunning
        private val logDataPrivate = MutableLiveData<String>()
        val logData: LiveData<String> = logDataPrivate
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        fun start(context: Context) {
            val intent = Intent(context, HTTPtunnelIntentService::class.java)
            intent.action = ACTION_STARTNOVPN
            if (VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun checkStatus(context: Context?) {
            val localIntent = Intent(ACTION_RESUMEACTIVITY)
            // Broadcasts the Intent to receivers in this app.


            LocalBroadcastManager.getInstance(context!!).sendBroadcast(localIntent)
        }
    }
}