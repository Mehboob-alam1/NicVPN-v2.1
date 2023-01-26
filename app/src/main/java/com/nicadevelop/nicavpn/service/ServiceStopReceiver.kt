package com.nicadevelop.nicavpn.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ServiceStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val intentStop = Intent(context, HTTPtunnelIntentService::class.java)
        context.stopService(intentStop)
    }
}