package com.nicadevelop.nicavpn;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class CountDownTimer {

    private TimeUnit timeUnit;
    private Long startValue;
    private Disposable disposable;

    public CountDownTimer(Long startValue, TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        this.startValue = startValue;
    }

    public abstract void onTick(long tickValue);

    public abstract void onFinish();

    public void start() {
        Observable.zip(Observable.range(0, startValue.intValue()), io.reactivex.Observable.interval(1, timeUnit), (integer, aLong) -> {
            return (Long) (startValue - integer);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Long aLong) {
                onTick(aLong);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                onFinish();
            }
        });
    }

    public void cancel() {
        if (disposable != null) disposable.dispose();
    }
}


///*
// * Copyright (C) 2008 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.nicadevelop.nicavpn;
//
//import android.util.Log;
//import android.os.Handler;
//import android.os.SystemClock;
//import android.os.Message;
//
///**
// * Schedule a countdown until a time in the future, with
// * regular notifications on intervals along the way.
// * <p>
// * Example of showing a 30 second countdown in a text field:
// *
// * <pre class="prettyprint">
// * new CountdownTimer(30000, 1000) {
// *
// *     public void onTick(long millisUntilFinished) {
// *         mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
// *     }
// *
// *     public void onFinish() {
// *         mTextField.setText("done!");
// *     }
// *  }.start();
// * </pre>
// * <p>
// * The calls to {@link #onTick(long)} are synchronized to this object so that
// * one call to {@link #onTick(long)} won't ever occur before the previous
// * callback is complete.  This is only relevant when the implementation of
// * {@link #onTick(long)} takes an amount of time to execute that is significant
// * compared to the countdown interval.
// */
//public abstract class CountDownTimer {
//
//    /**
//     * Millis since epoch when alarm should stop.
//     */
//    private final long mMillisInFuture;
//
//    /**
//     * The interval in millis that the user receives callbacks
//     */
//    private final long mCountdownInterval;
//
//    private long mStopTimeInFuture;
//
//    private boolean mCancelled = false;
//
//    /**
//     * @param millisInFuture    The number of millis in the future from the call
//     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
//     *                          is called.
//     * @param countDownInterval The interval along the way to receive
//     *                          {@link #onTick(long)} callbacks.
//     */
//    public CountDownTimer(long millisInFuture, long countDownInterval) {
//        mMillisInFuture = millisInFuture;
//        mCountdownInterval = countDownInterval;
//    }
//
//    /**
//     * Cancel the countdown.
//     * <p>
//     * Do not call it from inside CountDownTimer threads
//     */
//    public final void cancel() {
//        mHandler.removeMessages(MSG);
//        mCancelled = true;
//    }
//
//    /**
//     * Start the countdown.
//     */
//    public synchronized final CountDownTimer start() {
//        if (mMillisInFuture <= 0) {
//            onFinish();
//            return this;
//        }
//        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
//        mHandler.sendMessage(mHandler.obtainMessage(MSG));
//        mCancelled = false;
//        return this;
//    }
//
//
//    /**
//     * Callback fired on regular interval.
//     *
//     * @param millisUntilFinished The amount of time until finished.
//     */
//    public abstract void onTick(long millisUntilFinished);
//
//    /**
//     * Callback fired when the time is up.
//     */
//    public abstract void onFinish();
//
//
//    private static final int MSG = 1;
//
//
//    // handles counting down
//    private Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            synchronized (CountDownTimer.this) {
//                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
//
//                if (millisLeft <= 0) {
//                    onFinish();
//                } else if (millisLeft < mCountdownInterval) {
//                    // no tick, just delay until done
//                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
//                } else {
//                    long lastTickStart = SystemClock.elapsedRealtime();
//                    onTick(millisLeft);
//
//                    // take into account user's onTick taking time to execute
//                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();
//
//                    // special case: user's onTick took more than interval to
//                    // complete, skip to next interval
//                    while (delay < 0) delay += mCountdownInterval;
//
//                    if (!mCancelled) {
//                        sendMessageDelayed(obtainMessage(MSG), delay);
//                    }
//                }
//            }
//        }
//    };
//}