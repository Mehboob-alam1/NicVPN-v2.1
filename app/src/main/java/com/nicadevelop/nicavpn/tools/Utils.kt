package com.nicadevelop.nicavpn.tools

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageInfo
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import com.google.android.material.textfield.TextInputLayout
import com.nicadevelop.nicavpn.*
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsKeys
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmSharedPreferences
import com.sdsmdg.tastytoast.TastyToast

import kotlinx.android.synthetic.main.advance_settings_dialog.*
import kotlinx.android.synthetic.main.advance_settings_dialog.view.*
import kotlinx.android.synthetic.main.layout_edit_text_dialog.*
import kotlinx.android.synthetic.main.layout_edit_text_dialog.view.adView
import kotlinx.android.synthetic.main.layout_edit_text_dialog.view.btnCancel
import kotlinx.android.synthetic.main.layout_edit_text_dialog.view.btnOk
import java.io.*
import java.net.InetAddress
import java.net.NetworkInterface
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.content.Context.POWER_SERVICE
import android.os.*

import android.os.Build

import com.appodeal.ads.Appodeal


@Suppress("unused")
object Utils {
    private const val TAG = "Utils."
    fun createAlertDialog(
        context: Context,
        title: String,
        message: CharSequence,
        persistent: Boolean,
        dialogClickListener: DialogClickListener?
    ) {
        createAlertDialog(context, title, message, persistent, "Ok", dialogClickListener)
    }

    fun createAlertDialog(
        context: Context,
        title: String,
        message: CharSequence,
        persistent: Boolean,
        positiveButtonContent: String?,
        dialogClickListener: DialogClickListener?
    ) {
        var buttonContent: String? = positiveButtonContent
        if (buttonContent == null) buttonContent = "Ok"
        val builder = Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
            .setPositiveButton(buttonContent) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                dialogClickListener?.onDialogPositiveClickListener()
            }
        builder.setCancelable(!persistent)
        val dialog = builder.create()
        dialog.show()
    }

    fun createCustomAlertDialog(
        context: Context,
        title: String,
        message: CharSequence,
        timeToShow: Int
    ) {
        val builder = Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
            .setPositiveButton("Ok (${timeToShow})") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        var timeRemaining = timeToShow
        val mHandler = Handler()
        mHandler.postDelayed(object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                if (timeRemaining != 0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).text = "Ok (${timeRemaining})"
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    mHandler.postDelayed(this, 1000)
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).text = "Ok"
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                }
                timeRemaining--
            }
        }, 0)
    }

    fun createAlertDialog(
        context: Context,
        title: String,
        message: CharSequence,
        persistent: Boolean,
        positiveButtonContent: String?,
        dialogClickListener: DialogClickListener?,
        cancelButton: Boolean
    ) {
        var buttonContent: String? = positiveButtonContent
        if (buttonContent == null) buttonContent = "Ok"
        val builder = Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
            .setPositiveButton(buttonContent) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                dialogClickListener?.onDialogPositiveClickListener()
                val activity = context as Activity
                Appodeal.show(activity, Appodeal.BANNER_VIEW)
            }
        if (cancelButton) {
            builder.setNegativeButton(
                context.getString(R.string.dialog_cancel)
            ) { dialog, _ ->
                dialog.dismiss()
                val activity = context as Activity
                Appodeal.show(activity, Appodeal.BANNER_VIEW)
            }
        }
        builder.setCancelable(!persistent)
        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("InflateParams")
    fun createEditTextDialog(
        context: Context,
        dialogTextClickListener: DialogTextClickListener?
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.layout_edit_text_dialog, null)
        val builder = Builder(context)
        builder.setView(dialogView)
        builder.setTitle(context.getString(R.string.action_export))
        builder.setCancelable(false)
        val dialog = builder.create()

        @Suppress("ConstantConditionIf")
        if (true) {
            /*createAdBannerView(context, BuildConfig.AD_BANNER_2_KEY, dialogView.adView)*/
            //createAdBannerView(context, BuildConfig.AD_BANNER_3_KEY, dialogView.adView)
            AppoDealAdManager.Instance().showBannerAd(context as Activity,dialogView.adView)

        }
        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()

            val activity = context as Activity
            Appodeal.show(activity, Appodeal.BANNER_VIEW)
        }

        dialogView.btnOk.setOnClickListener {
            DmSharedPreferences.getInstance(context)!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_BLOCK_CONF_SERVER,
                dialog.checkBlockServer.isChecked
            )

            dialogTextClickListener?.onDialogPositiveClickListener(
                dialog.etFileName.text.toString(),
                dialog.etMessage.text.toString(),
                dialog
            )

            val activity = context as Activity
            Appodeal.show(activity, Appodeal.BANNER_VIEW)
        }

        dialog.show()
    }

/*
    @SuppressLint("InflateParams")
    fun batteryWarningDialog(context: Context) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.layout_battery_warning_dialog, null)

        val builder = Builder(context)
        builder.setView(dialogView)
        builder.setCancelable(false)

        dialogView.fix_background_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent()
                val packageName: String = context.packageName
                val pm: PowerManager? = context.getSystemService(POWER_SERVICE) as PowerManager?
                if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    context.startActivity(intent)
                }
            } else {
                TastyToast.makeText(
                    context,
                    context.getString(R.string.settings_warning_not_marshmallow),
                    Toast.LENGTH_LONG, TastyToast.DEFAULT).show()
            }
            dialogView.btnCancel.isEnabled = true
            dialogView.btnOk.isEnabled = true
        }

        val dialog = builder.create()
        dialogView.btnCancel.isEnabled = false
        dialogView.btnOk.isEnabled = false

        AppoDealAdManager.Instance().showBannerAd(context as Activity, dialogView.adView)

        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
*/

    fun createAdvanceSettingsDialog(
        context: Context,
        dialogAdvanceSettingsClickListener: DialogAdvanceSettingsClickListener?
    ) {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.advance_settings_dialog, null)
        val builderAdvanceSettings = Builder(context)
        builderAdvanceSettings.setView(dialogView)
        builderAdvanceSettings.setTitle(context.getString(R.string.advance_settings))
        builderAdvanceSettings.setCancelable(false)
        val dialogAdvanceSettings = builderAdvanceSettings.create()
// activar debug
/*        LogUtil.e(
            LogUtil.DIAM_PROXY_TAG,
            "et_custom_dns: " + dialogView.etCustomDns.text.toString()
        )*/

        @Suppress("ConstantConditionIf")
        if (BuildConfig.IS_USE_ADS) {
            /*createAdBannerView(context, BuildConfig.AD_BANNER_2_KEY, dialogView.adView)*/
            //createAdBannerView(context, BuildConfig.AD_BANNER_3_KEY, dialogView.adView)
            AppoDealAdManager.Instance().showBannerAd(context as Activity,dialogView.adView)

        }
        dialogView.btnCancel.setOnClickListener {
            dialogAdvanceSettings.dismiss()

            val activity = context as Activity
            Appodeal.show(activity, Appodeal.BANNER_VIEW)
        }

        dialogView.btnOk.setOnClickListener {
            if (dialogAdvanceSettings.etCustomDns.text.toString().isEmpty()) {
                val textError: TextInputLayout =
                    dialogAdvanceSettings.input_layout_custom_dns as TextInputLayout
                textError.error = context.getString(R.string.enter_valid_dns)
            } else {
                dialogAdvanceSettingsClickListener?.onDialogPositiveClickListener(
                    dialogAdvanceSettings.etCustomDns.text.toString(),
                    dialogAdvanceSettings.checkActivateUdp.isChecked,
                    dialogAdvanceSettings.checkUseIP.isChecked,
                    dialogAdvanceSettings
                )
            }

            val activity = context as Activity
            Appodeal.show(activity, Appodeal.BANNER_VIEW)
        }

        dialogView.fix_background_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent()
                val packageName: String = context.packageName
                val pm: PowerManager? = context.getSystemService(POWER_SERVICE) as PowerManager?
                if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    context.startActivity(intent)
                }
            } else {
                TastyToast.makeText(
                    context,
                    context.getString(R.string.settings_warning_not_marshmallow),
                    Toast.LENGTH_LONG, TastyToast.DEFAULT).show()
            }
        }

        dialogAdvanceSettings.show()
    }

  /*  @SuppressLint("MissingPermission")
    fun isWifiConnected(context: Context): Boolean {
        // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "isWifiConnected")
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION") val networkInfo: NetworkInfo? =
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (networkInfo != null && networkInfo.isConnected) {
            // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "isWifiConnected: Wifi is connected!!!")
            return true
        }
*//*        v(
            LogUtil.DIAM_PROXY_TAG,
            TAG + "isWifiConnected: networkInfo is null or wifi not connected!!!"
        )*//*
        // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "isWifiConnected: connectivityManager is null")
        return false
    }

    fun findBinary(binaryName: String): Boolean {
        var found = false
        val places = arrayOf(
            "/sbin/",
            "/system/bin/",
            "/system/xbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/",
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/sbin/su/",
            "/system/bin/su",
            "/system/bin/su/",
            "/system/xbin/su",
            "/system/xbin/su/",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su",
            "/su/",
            "/data/local/xbin/",
            "/system/bin/.ext/",
            "/system/bin/failsafe/",
            "/system/sd/xbin/",
            "/su/xbin/",
            "/su/bin/",
            "/magisk/.core/bin/",
            "/system/usr/we-need-root/",
            "/system/xbin/",
            "/system/su",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su-backup",
            "/system/xbin/mu",
            "/system/su/",
            "/system/bin/.ext/.su/",
            "/system/usr/we-need-root/su-backup/",
            "/system/xbin/mu/"
        )
        for (where in places) {
            if (File(where + binaryName).exists()) {
                found = true
                break
            }
        }
        return found
    }

    fun verifyDeviceRoot(context: Context): Boolean {
        val rootBeer = RootBeer(context)
        return rootBeer.isRooted
    }*/

/*    fun isDebuggerConnected(context: Context): Boolean {
        return if (BuildConfig.DEBUG) false else detectDebugger() || isDebuggable(context) || isDevMode(
            context
        )
    }

    private fun isDebuggable(context: Context): Boolean {
        // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "isDebuggable")
        return context.applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    private fun detectDebugger(): Boolean {
        // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "detectDebugger")
        return Debug.isDebuggerConnected()
    }

    private fun isDevMode(context: Context): Boolean {
        // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "isDevMode")
        val isDeveloperModeActivate = Secure.getInt(
            context.contentResolver,
            Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
        v(
            LogUtil.DIAM_PROXY_TAG,
            TAG + "isDevMode.isDeveloperModeActivate: " + isDeveloperModeActivate
        )
        return isDeveloperModeActivate
    }*/

    @SuppressLint("DefaultLocale")
    fun getIPAddress(useIPv4: Boolean): String {
        // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "getIPAddress")
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in interfaces) {
                val inetAddresses: List<InetAddress> =
                    Collections.list(networkInterface.inetAddresses)
                for (inetAddress in inetAddresses) {
                    if (!inetAddress.isLoopbackAddress) {
                        val hostAddress: String = inetAddress.hostAddress
                        val isIPv4 = hostAddress.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return hostAddress
                        } else {
                            if (!isIPv4) {
                                val delim = hostAddress.indexOf('%') // drop ip6 zone suffix

                                return if (delim < 0) hostAddress.toUpperCase() else hostAddress.substring(
                                    0,
                                    delim
                                ).toUpperCase()
                            }
                        }
                    }
                }
            }
            return "0.0.0.0"
        } catch (error: Exception) {
            // v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "getIPAddress.error: " + error)
            return "0.0.0.0"
        }
    }

    fun openPageOnBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun openAppOnPlayStore(context: Context, appPackageName: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
        context.startActivity(intent)
    }

    fun bytesToHex(value: ByteArray): String {
        val sb = StringBuilder()
        for (b in value) {
            val hex: String = Integer.toHexString(0xff and b.toInt())
            if (hex.length == 1) sb.append('0')
            sb.append(hex)
        }
        return sb.toString()
    }

    fun hexToBytes(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun getSaltString(length: Int, separator: String): String {
        val SALTCHARS = "}}UrTYU%38[PJ%[PJö╝╕ï'╛?[hTd#v╢═¶ﺐﺼ"
        val salt = StringBuilder()
        val rnd = Random()
        while (salt.length < length) {
            val index = (rnd.nextFloat() * SALTCHARS.length).toInt()
            salt.append(SALTCHARS[index])
        }
        return separator + salt.toString()
    }

    interface DialogClickListener {
        fun onDialogPositiveClickListener()
    }

    interface DialogTextClickListener {
        fun onDialogPositiveClickListener(
            fileName: String,
            fileMessage: String,
            dialog: AlertDialog
        )
    }

    interface DialogAdvanceSettingsClickListener {
        fun onDialogPositiveClickListener(
            customDns: String,
            enableUDP: Boolean,
            enableServeByIp: Boolean,
            dialog: AlertDialog
        )
    }

    fun copyToClipboard(context: Context, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", content)
        clipboard.setPrimaryClip(clip)

        TastyToast.makeText(
            context, "\n" + context.resources.getString(R.string.dialog_key_auth_copied), Toast.LENGTH_LONG, TastyToast.DEFAULT).show()
    }

    fun generatePasswordKey(context: Context) {
        DmSharedPreferences.getInstance(context)!!.putString(
            DmPrefsKeys.SHARED_PREFERENCES_VPN_PASSWORD,
            DmPrefsKeys.SHARED_PREFERENCES_VPN_PASSWORD_DEFAULT
        )
    }

    fun isExternalStorageReadOnly(): Boolean {
        val extStorageState: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED_READ_ONLY == extStorageState
    }

    fun isExternalStorageAvailable(): Boolean {
        val extStorageState: String = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == extStorageState
    }

    fun getFileDirectory(): File {
        val filePath = "NicVPN"
        //var dir: File?
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + filePath
            )
        } else {
            File(Environment.getExternalStorageDirectory().absolutePath + File.separator + filePath)
        }
        // dir = File(Environment.getExternalStorageDirectory().absolutePath + File.separator + filePath)
        return dir
    }

    @ExperimentalStdlibApi
    fun saveExternalFile(fileName: String, value: String): Boolean {
        try {
            val file = getFileDirectory()

            if (!file.exists()) {
                // activar debug
/*                LogUtil.e(
                    LogUtil.DIAM_PROXY_TAG,
                    "$TAG.saveExternalFile.directory: Creando directorio...."
                )*/
                file.mkdir()
            }

            val outFileName: String = file.path + File.separator + fileName + Constants.EXT_FILE
// activar debug
/*            LogUtil.e(
                LogUtil.DIAM_PROXY_TAG,
                "$TAG.saveExternalFile.outFileName: $outFileName"
            )*/

            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(outFileName, false))
            bufferedOutputStream.write(encrypt(value.encodeToByteArray()))
            bufferedOutputStream.flush()
            bufferedOutputStream.close()
            return true
        } catch (exception: Exception) {
            return false
        }
    }

    @Throws(IOException::class)
    fun readExternalFile(context: Context, uri: Uri?): ByteArray? {
        context.contentResolver.openInputStream(uri!!)?.use { inputStream ->
            val fileContents = inputStream.readBytes()
            val inputBuffer = BufferedInputStream(inputStream)
            inputBuffer.read(fileContents)
            inputBuffer.close()
            return decrypt(fileContents)
        }
        return null
    }

    fun readExternalFile(filePath: String): ByteArray {
        //LogUtil.//e(LogUtil.DIAM_PROXY_TAG // activar debug // activar debug, "$TAG.readExternalFile.filePath: $filePath")
        val file = File(filePath)
        val fileContents = file.readBytes()
        val inputBuffer = BufferedInputStream(
            FileInputStream(file)
        )

        inputBuffer.read(fileContents)
        inputBuffer.close()

        return decrypt(fileContents)
    }

    fun encryptValue(value: Any?): String {
        var hashInBytes = value.toString().toByteArray(StandardCharsets.UTF_8)
        val mValue = bytesToHex(hashInBytes) + getSaltString(1845, "=")
        hashInBytes = mValue.toByteArray(StandardCharsets.UTF_8)
        return bytesToHex(hashInBytes)
    }

    fun decryptValue(value: String): String {
        var hashInBytes = hexToBytes(value)
        val mValue = String(hashInBytes).split("=").toTypedArray()
        hashInBytes = hexToBytes(mValue[0])
        return String(hashInBytes)
    }

    @SuppressLint("GetInstance")
    private fun encrypt(fileData: ByteArray): ByteArray {
        val data = getSecretKey().encoded
        val secretKeySpec = SecretKeySpec(data, 0, data.size, "AES")
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            secretKeySpec,
            IvParameterSpec(ByteArray(cipher.blockSize))
        )
        return cipher.doFinal(fileData)
    }

    @SuppressLint("GetInstance")
    fun decrypt(fileData: ByteArray): ByteArray {
        val decrypted: ByteArray
        val cipher = Cipher.getInstance("AES", "BC")
        cipher.init(
            Cipher.DECRYPT_MODE,
            getSecretKey(),
            IvParameterSpec(ByteArray(cipher.blockSize))
        )
        decrypted = cipher.doFinal(fileData)
        return decrypted
    }

    fun generateSecretKey(): SecretKey? {
        val secureRandom = SecureRandom()
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator?.init(128, secureRandom)
        val secretKey = keyGenerator?.generateKey()
        val encodedKey =
            android.util.Base64.encodeToString(secretKey!!.encoded, android.util.Base64.NO_WRAP)
        return secretKey
    }

    private fun getSecretKey(): SecretKey {
        val decodedKey =
            android.util.Base64.decode(Constants.AES_FILE_KEY, android.util.Base64.NO_WRAP)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    }

    @SuppressLint("WrongConstant")
    fun vb(context: Context): String? {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 128)
            packageInfo.versionName.toString() + " Build " + packageInfo.versionCode
        } catch (e: java.lang.Exception) {
            "-"
        }
    }

    /*
        Returns the latency to a given server in mili-seconds by issuing a ping command.
        system will issue NUMBER_OF_PACKTETS ICMP Echo Request packet each having size of 56 bytes
        every second, and returns the avg latency of them.
        Returns 0 when there is no connection
    */
    fun getLatency(context: Context, ipAddress: String) {
        object : Thread() {
            override fun run() {
                val NUMBER_OF_PACKTETS = 5
                val pingCommand =
                    "/system/bin/ping -c $NUMBER_OF_PACKTETS $ipAddress"
                var inputLine = ""
                var avgRtt = 0.0
                try {
                    // execute the command on the environment interface
                    val process = Runtime.getRuntime().exec(pingCommand)
                    // gets the input stream to get the output of the executed command
                    val bufferedReader =
                        BufferedReader(InputStreamReader(process.inputStream))
                    inputLine = bufferedReader.readLine()
                    //LogUtil.//e(LogUtil.DIAM_PROXY_TAG // activar debug // activar debug, "getLatency: $inputLine")
                    @Suppress("SENSELESS_COMPARISON")
                    while (inputLine != null) {
                        if (inputLine.length > 0 && inputLine.contains("avg")) {  // when we get to the last line of executed ping command
                            break
                        }
                        inputLine = bufferedReader.readLine()
                    }
                } catch (e: IOException) {
                    //LogUtil.//e(LogUtil.DIAM_PROXY_TAG // activar debug // activar debug, "$TAG.getLatency: EXCEPTION")
                } catch (e: java.lang.Exception) {
                    //LogUtil.//e(LogUtil.DIAM_PROXY_TAG // activar debug // activar debug, "$TAG.getLatency: EXCEPTION")
                }

                if (!inputLine.isEmpty()) {
                    // Extracting the average round trip time from the inputLine string
                    try {
                        val afterEqual =
                            inputLine.substring(inputLine.indexOf("="), inputLine.length)
                                .trim { it <= ' ' }
                        val afterFirstSlash =
                            afterEqual.substring(afterEqual.indexOf('/') + 1, afterEqual.length)
                                .trim { it <= ' ' }
                        val strAvgRtt = afterFirstSlash.substring(0, afterFirstSlash.indexOf('/'))
                        avgRtt = java.lang.Double.valueOf(strAvgRtt)
                    } catch (e: java.lang.Exception) {
                        //LogUtil.//e(LogUtil.DIAM_PROXY_TAG // activar debug // activar debug, "$TAG.inputLine: EXCEPTION")
                    }
                }
                LogFragment.addLog(
                    context.getString(R.string.ping_latency) + " " + avgRtt + " ms"
                )
                return
            }
        }.start()
    }
}
