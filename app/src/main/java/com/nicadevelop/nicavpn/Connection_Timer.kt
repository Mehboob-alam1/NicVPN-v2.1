package com.nicadevelop.nicavpn

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import com.nicadevelop.nicavpn.tools.LogUtil.e
import java.util.*

class Connection_Timer : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    private val timer = Timer()
    private var mHandler: Handler = Handler()
    var value: Long = 0
    var countDownTimer: CountDownTimer? = null

    var bi: Intent = Intent("com.nicadevelop.nicavpn.countdown_br")

    override fun onCreate() {
        e("value_intent_3", "onStartCommand");
        super.onCreate()
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        e("value_intent_1", "onStartCommand");

        if (intent.extras != null) {
            value = intent.extras!!.getLong("milliseconds");
            e("value_intent2", "" + value);
        }

        if (value == 0L)
            value = 60000;

        e("initial_start_23", "Countdown seconds remaining: $value");

        timer.scheduleAtFixedRate(TimeTask(value), 5, value)
        return START_NOT_STICKY
    }

    /*override fun onTaskRemoved(rootIntent: Intent?) {
        e("initial_start_2389", "onTaskRemoved");

        timer.cancel()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onTaskRemoved(rootIntent)
    }*/

    override fun onDestroy() {
        e("initial_start_23000", "onTaskRemoved");

        timer.cancel()
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy()
    }

    private inner class TimeTask(private var time: Long) : TimerTask() {
        override fun run() {

            if (mHandler==null)
                mHandler= Handler()

            mHandler.post {
                if (countDownTimer != null)
                    countDownTimer!!.cancel()

                countDownTimer = object : CountDownTimer(time, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        e(
                            "initial_start_15",
                            "Countdown seconds remaining:" + millisUntilFinished / 1000
                        )

                        if (millisUntilFinished / 1000 != 0L) {
                            bi.putExtra("countdown", millisUntilFinished);
                            sendBroadcast(bi);
                        } else {
                            e(
                                "initial_start_16",
                                "Countdown seconds remaining: " + millisUntilFinished / 1000
                            );

                            if (countDownTimer != null)
                                countDownTimer!!.cancel()

                            bi.putExtra("countdown", 0);
                            sendBroadcast(bi);
                        }
//                    bi.putExtra("countdown", millisUntilFinished)
//                    sendBroadcast(bi)
                    }

                    override fun onFinish() {

                        if (countDownTimer != null)
                        mHandler.removeCallbacksAndMessages(null);
                        countDownTimer!!.cancel()

                        bi.putExtra("countdown", 0);
                        sendBroadcast(bi);
                        timer.cancel()
                    }
                }
                countDownTimer!!.start()
            }


//            time--  //12000
//            e("initial_start_5", "Countdown seconds remaining: ${time /1000}");
//            bi.putExtra("countdown", time)
//            sendBroadcast(bi)


            /* if (time != time-60) {
                 e("initial_start_77", "Countdown seconds remaining: $time");
                 bi.putExtra("countdown", time)
                 sendBroadcast(bi)
             } else {
                 e("initial_start_78", "Countdown seconds remaining: $time");
                 bi.putExtra("countdown", 0);
                 sendBroadcast(bi);
                 timer.cancel()
             }*/
        }
    }


//    companion object {
//        const val COUNTDOWN_BR = "com.nicadevelop.nicavpn.countdown_br"
//    }

}