package com.nicadevelop.nicavpn.service

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
import com.nicadevelop.nicavpn.*
import com.nicadevelop.nicavpn.Constants.CONFIG
import com.nicadevelop.nicavpn.Constants.DEF_CONFIG
import com.nicadevelop.nicavpn.Constants.EXECUTABLE
import com.nicadevelop.nicavpn.Constants.PID
import com.nicadevelop.nicavpn.tools.LogUtil
import com.nicadevelop.nicavpn.tools.LogUtil.d
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsKeys
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmSharedPreferences
import okio.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InterruptedIOException
import java.util.*

class HTTProcessManager {
    private var stunnelProcess: Process? = null
    private var stopService = false
    private fun getCustomPing(context: Context): Boolean {
        return DmSharedPreferences.getInstance(context)!!.getBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING,
            DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING_DEFAULT
        )
    }

    private fun getConfiguredServer(context: Context): String {
        var configuredServer: String = DmSharedPreferences.getInstance(context)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_SERVER, DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
        ).toString() + ":" + DmSharedPreferences.getInstance(context)!!.getLong(
            DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT,
            DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT_DEFAULT
        )
        if (DmSharedPreferences.getInstance(context)!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_DNS_CONNECTION,
                DmPrefsKeys.SHARED_PREFERENCES_DNS_CONNECTION_DEFAULT
            )
        ) {
            configuredServer = DmSharedPreferences.getInstance(context)!!.getString(
                DmPrefsKeys.SHARED_PREFERENCES_DNS, DmPrefsKeys.SHARED_PREFERENCES_DNS_DEFAULT
            ) + "." + configuredServer
        }
        return configuredServer
    }

    private fun getConfiguredSni(context: Context): String? {
        return DmSharedPreferences.getInstance(context)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_SNI, DmPrefsKeys.SHARED_PREFERENCES_SNI_DEFAULT
        )
    }

    private fun getConfiguredPing(context: Context): Long {
        return DmSharedPreferences.getInstance(context)!!.getLong(
            DmPrefsKeys.SHARED_PREFERENCES_PING, DmPrefsKeys.SHARED_PREFERENCES_PING_DEFAULT
        )
    }

    internal fun start(context: HTTPtunnelIntentService) {
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG========================================")
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "start!!!")
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG========================================")
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "start Verify developer mode active")
/*        if (isDebuggerConnected(context)) {
            val intentWifiConnected = Intent(MainActivity.ACTION_MAIN_DEVELOPER_SETTINGS)
            intentWifiConnected.putExtra(MainActivity.DEBUGGER_CONNECTED, true)
            context.sendBroadcast(intentWifiConnected)
        }*/
        val pidFile = File(context.filesDir.path + "/" + PID)
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "pidFile.exists(): " + pidFile.exists())
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Paso validacion")
        // checkAndExtract(context)
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "checkAndExtract")
        setupConfig(context, getCustomConfig(context))
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "setupConfig")
        context.clearLog()
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "clearLog")
        try {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "inicio try")
            val env = arrayOfNulls<String?>(0)
            val workingDirectory = File(context.filesDir.path)
            stunnelProcess = Runtime.getRuntime().exec(
                String.format(
                    Locale.US,
                    "%s/$EXECUTABLE %s/$CONFIG",
                    context.applicationInfo.nativeLibraryDir,
                    context.filesDir
                ), env, workingDirectory
            )

            readInputStream(context, stunnelProcess!!.errorStream.source().buffer())
            readInputStream(context, stunnelProcess!!.inputStream.source().buffer())
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Configured timeout")
            val timeout = getConfiguredPing(context)
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "getConfiguredPing: " + timeout)
            LogFragment.addLog(MyApplication.appContext.getString(R.string.getConfiguredPing) + "\r" + (timeout / 1000))

            /* Set up process I/O. */

            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Worker")
            val worker = Worker(stunnelProcess)
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "start")
            try {
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "join")
                if (getCustomPing(context)) {
                    worker.join(timeout)
                    worker.start()
                    if (worker.exit != null) {
                        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "worker.exit")
                    }
                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "sleep")
                    Thread.sleep(timeout)
                } else {
                    stunnelProcess!!.waitFor()
                }
            } catch (ex: InterruptedException) {
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Error: " + ex.toString())
                worker.interrupt()
                Thread.currentThread().interrupt()
                throw ex
            } finally {
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "finally")
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Destroy Forcibly!!!")
                    stunnelProcess!!.destroyForcibly()
                } else {
                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "destroy")
                    LogFragment.addLog(MyApplication.appContext.getString(R.string.destroy))
                    stunnelProcess!!.destroy()
                }
                if (!stopService) {
                    if (getCustomPing(context)) {
                        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Reconectando!!!")
                        LogFragment.addLog(MyApplication.appContext.getString(R.string.Reconectando))
                        start(context)
                    } else {
                        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG Only one connection!!!")
                    }
                } else {
                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Desconectado!!!")
                    LogFragment.addLog(MyApplication.appContext.getString(R.string.Desconectado))
                }
            }
        } catch (e: IOException) {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "failure", e)
        } catch (e: InterruptedException) {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "InterruptedException.e: " + e.toString())
            Thread.currentThread().interrupt()
        }
    }

    private fun getTypeConnection(context: Context): String? {
        return DmSharedPreferences.getInstance(context)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION,
            DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION_DEFAULT
        )
    }

    private fun isDnsConnection(context: Context): Boolean {
        return getTypeConnection(context) == context.getString(R.string.dns_connection)
    }

    private fun isDirectConnection(context: Context): Boolean {
        return getTypeConnection(context) == context.getString(R.string.direct_connection)
    }

    private fun getCustomConfig(context: Context): String {
        var customSni = getConfiguredSni(context)
        if (customSni != "") customSni = Constants.SNI_LABEL + customSni
        if (isDnsConnection(context) || isDirectConnection(context)) customSni = ""
        return (DEF_CONFIG
                // =================================================
                // Getting Custom Server
                // =================================================
                + Constants.SERVER_LABEL + getConfiguredServer(context) + "\n"
                // =================================================
                // Getting Custom SNI
                // =================================================
                + customSni)
    }

    private class Worker internal constructor(private val process: Process?) : Thread() {
        internal var exit: Int? = null
        override fun run() {
            try {
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Worker.run")
                exit = process!!.waitFor()
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Worker.run2")
            } catch (e: Exception) {
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Worker.error: " + e)
            }
        }

        init {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Worker.Constructor")
        }
    }

    internal fun stop(context: Context) {
        stopService = true
        if (stunnelProcess != null) {
            stunnelProcess!!.destroy()
        }
        val pidFile = File(context.filesDir.path + "/" + PID)
        try {
            if (pidFile.exists()) { // still alive!

                var pid: String? = null
                try {
                    pidFile.source().buffer().use { `in` -> pid = `in`.readUtf8Line() }
                } catch (e: IOException) {
                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "IOException: Failed to read PID file", e)
                } catch (e: Exception) {
                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Exception: Failed to read PID file", e)
                } catch (e: FileNotFoundException) {
                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "FileNotFoundException: Failed to read PID file", e)
                }
                if (pid == null || pid!!.trim { it <= ' ' } != "") {
                    d(LogUtil.DIAM_PROXY_TAG, TAG + "Attmepting to kill proxycry, pid = " + pid)
                    try {
                        Runtime.getRuntime().exec("kill $pid").waitFor()
                    } catch (e: Exception) {
                        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Failed to kill proxycry", e)
                    }
                    if (pidFile.exists()) {
                        // presumed dead, remove pid

                        pidFile.delete()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "File Not Found", e)
        }
    }

    companion object {
        private const val TAG = "StunnelProcessManager."
        private fun hasBeenUpdated(context: Context): Boolean {
            val sharedPreferences: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context)
            val versionCode = sharedPreferences.getInt("VERSION_CODE", 0)
            if (versionCode != BuildConfig.VERSION_CODE) {
                sharedPreferences.edit().putInt("VERSION_CODE", BuildConfig.VERSION_CODE).apply()
                return true
            }
            return false
        }

        fun checkAndExtract(context: Context) {
            val execFile = File(context.filesDir.path + "/" + EXECUTABLE)
            if (execFile.exists() && !hasBeenUpdated(context)) {
                return  // already extracted
            }



            Objects.requireNonNull(execFile.parentFile).mkdir()

            // Extract proxycry exectuable


            val am: AssetManager = context.assets
            try {
                am.open(EXECUTABLE).source().buffer().use { `in` ->
                    execFile.sink().buffer().use { out ->
                        out.writeAll(`in`)



                        execFile.setExecutable(true)
                        d(LogUtil.DIAM_PROXY_TAG, TAG + "Extracted proxycry binary successfully")
                    }
                }
            } catch (e: Exception) {
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Failed proxycry extraction: ", e)
            }
        }

        fun setupConfig(context: Context, config: String): Boolean {

            //Ver SNI + DNS
            //e(LogUtil.DIAM_PROXY_TAG, TAG + "setupConfig.config: " + config)
            val configFile = File(context.filesDir.path + "/" + CONFIG)
            /*if (configFile.exists()) {
            return true; // already created
        }*/



            Objects.requireNonNull(configFile.parentFile).mkdir()
            try {
                configFile.sink().buffer().use { out ->
                    val pidPath = context.filesDir.path + "/" + PID
                    val DEF_CONFIG_TMP = config.replace("[PID_KEY]", pidPath)
                    out.writeUtf8(DEF_CONFIG_TMP)
                    /*out.writeUtf8(context.getFilesDir().getPath());
            out.writeUtf8("/");
            out.writeUtf8(PID);*/

                    return true
                }
            } catch (e: IOException) {
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Failed config file creation: ", e)
                return false
            }
        }

        fun setupConfig(context: Context): Boolean {
            return setupConfig(context, DEF_CONFIG)
        }

        @Synchronized
        private fun readInputStream(context: HTTPtunnelIntentService, `in`: BufferedSource) {
            val streamReader: Thread = object : Thread() {
                override fun run() {
                    var line: String
                    try {
                        while (`in`.readUtf8Line().also { line = it!! } != null) {
                            context.appendLog(line)
                        }
                    } catch (e: IOException) {
                        //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "Error reading proxycry stream: $e")
                        if (e is InterruptedIOException) {
                            // This is fine, it quit
                            return
                        }
                    }
                }
            }
            streamReader.start()
        }
    }
}