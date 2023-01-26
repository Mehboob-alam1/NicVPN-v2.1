package com.nicadevelop.nicavpn.tools

import android.util.Log
import com.nicadevelop.nicavpn.BuildConfig

object LogUtil {
    const val DIAM_PROXY_TAG = "NdCheckMate"
    private const val DEBUG = Log.DEBUG
    private const val WARN = Log.WARN
    private const val VERBOSE = Log.VERBOSE
    private const val INFO = Log.INFO
    private const val ERROR = Log.ERROR
    /**
     * Send a [.VERBOSE] log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun v(tag: String, msg: String) {
        println(VERBOSE, tag, msg)
    }

    /**
     * Send a [.VERBOSE] log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun v(tag: String, msg: String, tr: Throwable?) {
        println(VERBOSE, tag, msg + '\n'
                .toString() + Log.getStackTraceString(tr))
    }

    /**
     * Send a [.DEBUG] log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun d(tag: String, msg: String) {
        println(DEBUG, tag, msg)
    }

    /**
     * Send a [.DEBUG] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun d(tag: String, msg: String, tr: Throwable?) {
        println(DEBUG, tag, msg + '\n'
                .toString() + Log.getStackTraceString(tr))
    }

    /**
     * Send an [.INFO] log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun i(tag: String, msg: String) {
        println(INFO, tag, msg)
    }

    /**
     * Send a [.INFO] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun i(tag: String, msg: String, tr: Throwable?) {
        println(INFO, tag, msg + '\n'
                .toString() + Log.getStackTraceString(tr))
    }

    /**
     * Send a [.WARN] log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun w(tag: String, msg: String) {
        println(WARN, tag, msg)
    }

    /**
     * Send a [.WARN] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun w(tag: String, msg: String, tr: Throwable?) {
        println(WARN, tag, msg)
        println(WARN, tag, Log.getStackTraceString(tr))
    }

    /**
     * Send an [.ERROR] log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun e(tag: String, msg: String) {
        println(ERROR, tag, msg)
    }

    /**
     * Send a [.ERROR] log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    fun e(tag: String, msg: String, tr: Throwable?) {
        println(ERROR, tag, msg)
        println(ERROR, tag, Log.getStackTraceString(tr))
    }

    /**
     * Low-level logging call.
     *
     * @param level The priority/type of this log message
     * @param tag   Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg   The message you would like logged.
     */
    private fun println(level: Int, tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.println(level, tag, msg)
        }
    }
}