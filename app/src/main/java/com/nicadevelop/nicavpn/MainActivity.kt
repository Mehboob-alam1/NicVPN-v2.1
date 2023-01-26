@file:Suppress("ConstantConditionIf")

package com.nicadevelop.nicavpn

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.VpnService
import android.os.*
import android.os.StrictMode.VmPolicy
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.nicadevelop.nicavpn.Premium_Feature.Premium_dialog
import com.nicadevelop.nicavpn.http.request.ClientServer
import com.nicadevelop.nicavpn.http.request.ServiceControl
import com.nicadevelop.nicavpn.logger.Log
import com.nicadevelop.nicavpn.models.Profile
import com.nicadevelop.nicavpn.service.*
import com.nicadevelop.nicavpn.tools.LogUtil
import com.nicadevelop.nicavpn.tools.LogUtil.e
import com.nicadevelop.nicavpn.tools.Utils
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsKeys
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsMethods
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmSharedPreferences
import com.nicadevelop.nicavpn.vpn.util.ProfileManager
import com.nicadevelop.nicavpn.vpn.util.Utility
import com.sdsmdg.tastytoast.TastyToast
import com.tencent.mmkv.MMKV
import com.v2ray.ang.AppConfig
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.util.Utils.stopVService
import com.v2ray.ang.viewmodel.MainViewModel
import io.michaelrocks.paranoid.Obfuscate
import kotlinx.android.synthetic.main.layout_edit_text_dialog.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


@Obfuscate
class MainActivity : AppCompatActivity(), ServiceConnection {
    private val TAG = MainActivity::class.java.simpleName + "."

    /**
     * ===============================================================
     * Socks 5
     * ===============================================================
     */
    ///region 'Socks 5'

    private var mManager: ProfileManager? = null
    private var mProfile: Profile? = null
    private var mBinder: IVpnService? = null
    private var mRunning = false
    private var mStarting = false
    private var mStopping = false
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(p1: ComponentName, binder: IBinder) {
            mBinder = IVpnService.Stub.asInterface(binder)

            try {
                mRunning = mBinder!!.isRunning
            } catch (e: Exception) {
            }

            if (mRunning) {
                updateState()
            }
        }

        override fun onServiceDisconnected(p1: ComponentName) {
            mBinder = null
        }
    }
    ///endregion
    /**
     * ===============================================================
     * Fin Socks 5
     * ===============================================================
     */
    private val defaultSharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    private var brcWifiConnected: BroadcastReceiver? = null
    private var brcWifiApStatus: BroadcastReceiver? = null
    private var brcVpnConnected: BroadcastReceiver? = null
    private var brcOSAnsADS: BroadcastReceiver? = null
    private var brcSettingDeveloperMode: BroadcastReceiver? = null
    private var navDrawer: DrawerLayout? = null
    private var men_btn_click: LinearLayout? = null
    private var vip_btn: ImageView? = null
    private var needUnbind = false
    private var serviceControl: ServiceControl? = null
    var server: ClientServer? = null
    var btnStartVpnMode: Button? = null
    var intentImport: Intent? = null
    var startFragment: StartFragment? = null

    private val mainStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_MAIN,
            MMKV.MULTI_PROCESS_MODE
        )
    }

    private val settingsStorage by lazy {
        MMKV.mmkvWithID(
            MmkvManager.ID_SETTING,
            MMKV.MULTI_PROCESS_MODE
        )
    }

    private val requestVpnPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                // startV2Ray()
                TastyToast.makeText(
                    this@MainActivity,
                    getString(R.string.reconnect),
                    Toast.LENGTH_LONG,
                    TastyToast.ERROR
                ).show()
            }
        }
    val mainViewModel: MainViewModel by viewModels()

    private fun stackOverflow(error: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
        val sendIntent = Intent()
        this.startActivity(sendIntent)
        exitProcess(0)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        // Frida detect
        System.loadLibrary(Constants.LibUltima)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        defaultSharedPreferences.edit().putBoolean(AppConfig.PREF_BYPASS_APPS, true).apply()

        /* val consent = if (savedInstanceState == null) {
             intent.getBooleanExtra("concent", false)
         } else {
             val consentStatus: Consent.Status = ConsentManager.getInstance(this).consentStatus
             (consentStatus === Consent.Status.PERSONALIZED
                     || consentStatus === Consent.Status.PARTLY_PERSONALIZED)
         }

         Appodeal.setTesting(BuildConfig.DEBUG)
         Appodeal.setLogLevel(Log.LogLevel.debug)
         Appodeal.setAutoCache(Appodeal.BANNER_VIEW, true)
         Appodeal.setAutoCache(Appodeal.INTERSTITIAL, true)
         Appodeal.initialize(this, AppoDealAdManager.APP_KEY, Appodeal.INTERSTITIAL, consent)
         Appodeal.initialize(this, AppoDealAdManager.APP_KEY, Appodeal.BANNER_VIEW, consent)
         Appodeal.setBannerViewId(R.id.appodealBannerView)*/

        /**
         * Generador de llave AES.
         * la llave generada se motrara en el log de la siguiente forma:
         * Utils..generateSecretKey: pxSjrbpEiqDOMxiYe3VcWQ==
         */
        //Utils.generateSecretKey()


        /**
         * ===============================================================
         * Socks 5
         * ===============================================================
         */
        ///region 'Socks 5'
        mManager = ProfileManager.getInstance(this)
        if (mProfile == null) {
            mProfile = mManager!!.default
        }
        ///endregion
        /**
         * ===============================================================
         * Fin Socks 5
         * ===============================================================
         */
        HTTProcessManager.setupConfig(this)

//        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val mViewPager: NonSwipeableViewPager = findViewById(R.id.container)
        navDrawer = findViewById(R.id.drawer_layout)
        men_btn_click = findViewById(R.id.men_btn_click)
        vip_btn = findViewById(R.id.vip_btn)
        val tabLayout: TabLayout = findViewById(R.id.tabs)
//        val navigationView: NavigationView = findViewById(R.id.navigationView)
//        setSupportActionBar(toolbar)
//        if (supportActionBar != null && !BuildConfig.IS_VPN_MODE) {
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        }

//        Appodeal.show(this@MainActivity, Appodeal.BANNER_VIEW)

        val mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mViewPager.adapter = mSectionsPagerAdapter
        tabLayout.setupWithViewPager(mViewPager)
        mViewPager.addOnPageChangeListener(onPageChangeListener)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.notification_channel)
            val description: String = getString(R.string.notification_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            channel.enableVibration(false)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        removeBroadcastVpn()
        val filterVpn = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        brcVpnConnected = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val extras = intent!!.extras
                if (extras != null) {
                    LogUtil.i(
                        LogUtil.DIAM_PROXY_TAG,
                        "mIRNetwork: ACTION_BACKGROUND_DATA_SETTING_CHANGED: " + extras.getString(
                            ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED
                        )
                    )
                    LogUtil.i(
                        LogUtil.DIAM_PROXY_TAG,
                        "mIRNetwork: CONNECTIVITY_ACTION: " + extras.getString(ConnectivityManager.CONNECTIVITY_ACTION)
                    )
                    LogFragment.addLog("APN: " + extras.getString(ConnectivityManager.EXTRA_EXTRA_INFO))
                    LogUtil.i(
                        LogUtil.DIAM_PROXY_TAG,
                        "mIRNetwork: EXTRA_EXTRA_INFO: " + extras.getString(ConnectivityManager.EXTRA_EXTRA_INFO)
                    )
                    LogUtil.i(
                        LogUtil.DIAM_PROXY_TAG,
                        "mIRNetwork: EXTRA_NO_CONNECTIVITY: " + extras.getString(ConnectivityManager.EXTRA_NO_CONNECTIVITY)
                    )
                    LogUtil.i(
                        LogUtil.DIAM_PROXY_TAG,
                        "mIRNetwork: EXTRA_REASON: " + extras.getString(ConnectivityManager.EXTRA_REASON)
                    )

                    if (extras.getString(ConnectivityManager.EXTRA_REASON) != null
                        && extras.getString(ConnectivityManager.EXTRA_REASON) == "agentDisconnect"
                    ) {
                        stopVpnProcess()
                    }
                }
                if (checkVPNStatus()) {
                    stopVpnProcess()
                }
            }
        }
        registerReceiver(brcVpnConnected, filterVpn)



        HTTPtunnelIntentService.isRunning.observe(this, Observer { aBoolean: Boolean ->
            if (aBoolean) {
                LogFragment.addLog(this@MainActivity.getString(R.string.IntentServiceRUN))
                startVpn()
            } else {
                LogFragment.addLog(this@MainActivity.getString(R.string.IntentServiceSTOP))
                stopVpn()
            }
        })

        val mainVpnMode: RelativeLayout = findViewById(R.id.main_vpn_mode)
        btnStartVpnMode = findViewById(R.id.btnStartVpnMode)
        @Suppress("ConstantConditionIf")
        if (BuildConfig.IS_VPN_MODE) {
            mainVpnMode.visibility = View.VISIBLE
            mViewPager.visibility = View.GONE
//            navigationView.visibility = View.GONE
            tabLayout.visibility = View.GONE
            btnStartVpnMode!!.visibility = View.VISIBLE
        }

        men_btn_click!!.setOnClickListener {
            val intent = Intent(this@MainActivity, Menu_Activity::class.java)
            startActivity(intent)
        }


        vip_btn!!.setOnClickListener {
            val dialog = Premium_dialog(this@MainActivity, R.style.AppTheme)
            dialog.show()
        }

        btnStartVpnMode!!.setOnClickListener {
            if (btnStartVpnMode!!.text == getString(R.string.payload_start)) {
                startVpn()
            } else {
                stopVpn()
                btnStartVpnMode!!.text = getString(R.string.payload_start)
            }
        }

        val intent = intent
        val scheme = intent.scheme
        if (scheme != null && (scheme == "file" || scheme == "content")) {
            val data = intent.data
            val file = File(data!!.path)

            if (!file.absolutePath.contains(Constants.EXT_FILE)) {
                TastyToast.makeText(
                    this,
                    getString(R.string.file_invalid),
                    Toast.LENGTH_LONG,
                    TastyToast.ERROR
                ).show()
            } else {
                intentImport = intent
            }
        }

        setupViewModelObserver()

        removeBroadcastVerifyOSAndADS()
        val osADSFilter = IntentFilter()
        osADSFilter.addAction("com.nicadevelop.nicavpn.OSADS")
        brcOSAnsADS = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
//                Appodeal.show(this@MainActivity, Appodeal.INTERSTITIAL)
            }
        }
        registerReceiver(brcOSAnsADS, osADSFilter)

        if (BuildConfig.BUILD_TYPE.contentEquals("debug")) {
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }

    private fun setupViewModelObserver() {
        mainViewModel.updateListAction.observe(this) {
            val index = it ?: return@observe
            if (index >= 0) {
                //adapter.notifyItemChanged(index)
            } else {
                //adapter.notifyDataSetChanged()
            }
        }

        mainViewModel.isRunning.observe(this) {
            val isRunning = it ?: return@observe
            //adapter.isRunning = isRunning
            if (isRunning) {
                /*binding.fab.setImageResource(R.drawable.ic_v)
                binding.tvTestState.text = getString(R.string.connection_connected)
                binding.layoutTest.isFocusable = true*/
            } else {
                /*binding.fab.setImageResource(R.drawable.ic_v_idle)
                binding.tvTestState.text = getString(R.string.connection_not_connected)
                binding.layoutTest.isFocusable = false*/
            }
        }
        mainViewModel.startListenBroadcast()
    }

    private fun stopVpnProcess() {
        stopService()
        stopTunnelService()
        if (startFragment != null) {
            startFragment!!.stopPayloadProcess()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            supportFragmentManager.fragments.forEach(fun(item) {
                item!!.onActivityResult(requestCode, resultCode, data)
            })
            return
        }*/
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { documentUri ->
                contentResolver.takePersistableUriPermission(
                    documentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                supportFragmentManager.fragments.forEach(fun(item) {
                    item!!.onActivityResult(requestCode, resultCode, data)
                })
            }
            return
        }
        /**
         * ===============================================================
         * Socks 5
         * ===============================================================
         */
        ///region 'Socks 5'
        if (resultCode == RESULT_OK) {
            Utility.startVpn(this, mProfile!!)
            checkState()
            btnStartVpnMode!!.text = getString(R.string.payload_stop)
        }
        ///endregion
        /**
         * ===============================================================
         * Fin Socks 5
         * ===============================================================
         */

    }

    /**
     * ===============================================================
     * Socks 5
     * ===============================================================
     */
    ///region 'Socks 5'
    private fun updateState() {
        if (mBinder == null) {
            mRunning = false
        } else {
            try {
                mRunning = mBinder!!.isRunning
            } catch (e: Exception) {
                mRunning = false
            }

        }


        @Suppress("ControlFlowWithEmptyBody")
        if (!mStarting && !mStopping || mStarting && mRunning || mStopping && !mRunning) {
            // mSwitch!!.isEnabled = true
        }

        if (mStarting && mRunning) {
            mStarting = false
        }

        if (mStopping && !mRunning) {
            mStopping = false
        }
    }

    public fun startVpn() {
        mStarting = true
        //val i = VpnService.prepare(this)
        LogFragment.addLog(this@MainActivity.getString(R.string.Startvpn))
        // CON ESTAS CONDICIONALES SE DETIENE EL VPN DE PAYLOAD
        //if (i != null) {
        /* startActivityForResult(i, 0)*/
        //} else {
        //    onActivityResult(0, Activity.RESULT_OK, null)
        //}

        val intent = VpnService.prepare(this)
        if (intent == null) {
            startV2Ray()
        } else {
            requestVpnPermission.launch(intent)
        }
//        Appodeal.show(this@MainActivity, Appodeal.INTERSTITIAL)
    }

    fun startV2Ray() {
        V2RayServiceManager.startV2Ray(this@MainActivity)
    }

    public fun stopVpn() {
        LogFragment.addLog(getString(R.string.Stopvpn))

        stopVService(this@MainActivity)
        if (mBinder == null)
            return

        mStopping = true

        try {
            mBinder!!.stop()
        } catch (e: Exception) {

        }

        mBinder = null

        LogFragment.addLog(this@MainActivity.getString(R.string.Stopvpn))
        unbindService(mConnection)
        checkState()
    }

    private fun checkState() {
        mRunning = false

        if (mBinder == null) {
            bindService(Intent(this, DmVpnService::class.java), mConnection, 0)
        }
    }
    ///endregion
    /**
     * ===============================================================
     * Fin Socks 5
     * ===============================================================
     */

    private fun removeBroadcastVerifyOSAndADS() {
        try {
            unregisterReceiver(brcOSAnsADS)
        } catch (e: Exception) {
        }
        if (brcOSAnsADS != null) brcOSAnsADS = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!BuildConfig.IS_VPN_MODE) {
            menuInflater.inflate(R.menu.menu_main, menu)
        }
        return true
    }

//    @ExperimentalStdlibApi
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                if (!navDrawer!!.isDrawerOpen(GravityCompat.START)) {
//                    navDrawer!!.openDrawer(GravityCompat.START)
//                } else {
//                    navDrawer!!.closeDrawer(GravityCompat.END)
//                }
//                true
//            }
//            R.id.mImport -> {
//                openDocumentPicker()
//                true
//            }
//            R.id.mExport -> {
//                //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected: Export")
//                if (DmSharedPreferences.getInstance(this@MainActivity)!!
//                        .getBoolean(DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF, false)
//                ) {
//                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: Configuration Imported....")
//                    /**
//                     * Cambiar el mensaje mostrado con el formato getString(R.string.files_not_ready)
//                     */
//                    TastyToast.makeText(
//                        this@MainActivity,
//                        getString(R.string.allowedexiste), Toast.LENGTH_LONG, TastyToast.ERROR
//                    ).show()
//                    return true
//                }
//                if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
//                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: File not available")
//                    TastyToast.makeText(
//                        this@MainActivity,
//                        getString(R.string.files_not_ready), Toast.LENGTH_LONG, TastyToast.WARNING
//                    ).show()
//                } else {
//                    askPermission(
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ) {
//                        Utils.createEditTextDialog(
//                            this@MainActivity,
//                            object : Utils.DialogTextClickListener {
//                                override fun onDialogPositiveClickListener(
//                                    fileName: String,
//                                    fileMessage: String,
//                                    dialog: AlertDialog
//                                ) {
//                                    if (fileName.isEmpty()) {
//                                        val textError: TextInputLayout =
//                                            dialog.input_layout_file as TextInputLayout
//                                        textError.error =
//                                            this@MainActivity.getString(R.string.enter_valid_file_name)
//                                        return
//                                    }
//                                    DmSharedPreferences.getInstance(this@MainActivity)!!
//                                        .putString(
//                                            DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG,
//                                            fileMessage
//                                        )
//
//                                    val dir =
//                                        Utils.getFileDirectory().path + File.separator + fileName + Constants.EXT_FILE
//                                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.Export.File: $dir")
//                                    val file = File(dir)
//                                    if (file.exists()) {
//                                        Utils.createAlertDialog(
//                                            this@MainActivity,
//                                            getString(R.string.action_export),
//                                            getString(R.string.the_file_already_exists),
//                                            true,
//                                            null,
//                                            object : Utils.DialogClickListener {
//                                                override fun onDialogPositiveClickListener() {
//                                                    saveExportFile(fileName)
//                                                    dialog.dismiss()
//                                                }
//                                            },
//                                            true
//                                        )
//                                    } else {
//                                        saveExportFile(fileName)
//                                        dialog.dismiss()
//                                    }
//                                }
//                            })
//                    }.onDeclined { error ->
//                        if (error.hasDenied()) {
//                            TastyToast.makeText(
//                                this@MainActivity,
//                                getString(R.string.permission_denied),
//                                Toast.LENGTH_LONG,
//                                TastyToast.WARNING
//                            ).show()
//                        }
//                        if (error.hasForeverDenied()) {
//                        }
//                    }
//                }
//                true
//            }
//            R.id.mRestore -> {
//                cacheDir.deleteRecursively()
//                clearPreferences()
//                //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected: Restore")
//                true
//            }
//            R.id.mDeleteLog -> {
//                //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected: Delete Log")
////                logFragment!!.clear()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, FILE_REQUEST_CODE)
    }

    @SuppressLint("SimpleDateFormat")
    @ExperimentalStdlibApi
    private fun saveExportFile(fileName: String) {
        val sdf = SimpleDateFormat("ddMyyyyhh:mm:ss")
        val currentDate = sdf.format(Date())
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected.fileId: $fileId")

        val typeConfig =
            DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
                DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION,
                DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION_DEFAULT
            )

        val server = DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_SERVER,
            DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
        )

        val port = DmSharedPreferences.getInstance(this@MainActivity)!!.getLong(
            DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT,
            DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT_DEFAULT
        )

        val host = DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_SNI,
            DmPrefsKeys.SHARED_PREFERENCES_SNI_DEFAULT
        )

        val dns = DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_DNS,
            DmPrefsKeys.SHARED_PREFERENCES_DNS_DEFAULT
        )

        val useCustomPing =
            DmSharedPreferences.getInstance(this@MainActivity)!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING,
                DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING_DEFAULT
            )

        val ping = DmSharedPreferences.getInstance(this@MainActivity)!!.getLong(
            DmPrefsKeys.SHARED_PREFERENCES_PING,
            DmPrefsKeys.SHARED_PREFERENCES_PING_DEFAULT
        )

        val payload = DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG,
            DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG_DEFAULT
        )

        val customMessage = DmSharedPreferences.getInstance(this@MainActivity)!!
            .getString(
                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG,
                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG_DEFAULT
            )

        val blockServer = DmSharedPreferences.getInstance(this@MainActivity)!!
            .getBoolean(DmPrefsKeys.SHARED_PREFERENCES_BLOCK_CONF_SERVER, false)

        val dnsTTHost = DmPrefsMethods.getDnsTTHost()
        val connDnsTTHost = DmPrefsMethods.getConnectionDnsTT()

        // encryptado shared preferencs
        val message = Utils.encryptValue(typeConfig) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(server) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(port) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(host) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(dns) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(useCustomPing) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(ping) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(payload) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(customMessage) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(blockServer) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(dnsTTHost) + Constants.FILE_SEPARATOR +
                Utils.encryptValue(connDnsTTHost) + Constants.FILE_SEPARATOR

        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected.message: $message")
        val encryptedValue = Utils.encryptValue(message)
// activar debug

/*        e(
            LogUtil.DIAM_PROXY_TAG,
            "$TAG.onOptionsItemSelected.encryptedValue: $encryptedValue"
        )*/

        var messageResponse = getString(R.string.data_export_error)
        if (Utils.saveExternalFile(fileName, encryptedValue)) {
            messageResponse = getString(R.string.data_export_successful)
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Toast.makeText(this@MainActivity, "Configuraciones exportadas exitosas en Documents !!", Toast.LENGTH_LONG).show()
        }else {
            Toast.makeText(this@MainActivity, messageResponse, Toast.LENGTH_LONG).show()
        }*/
        TastyToast.makeText(
            this@MainActivity,
            messageResponse,
            Toast.LENGTH_LONG,
            TastyToast.WARNING
        ).show()
    }

    private fun clearPreferences() {
        DmSharedPreferences.getInstance(this@MainActivity)!!.clear()
        DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_FIRST_INSTALLATION,
            false
        )
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    @SuppressLint("MissingPermission")
    private fun checkVPNStatus(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        } else {
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_VPN)!!.isConnectedOrConnecting
        }
    }

    private fun checkHooking() {
        try {
            throw Exception("blah")
        } catch (e: Exception) {
            var zygoteInitCallCount = 0
            for (stackTraceElement in e.stackTrace) {
                if (stackTraceElement.className == "com.android.internal.os.ZygoteInit") {
                    zygoteInitCallCount++
                    if (zygoteInitCallCount == 2) {
                        stackOverflow("HookDetection,Substrate is active on the device.")
                    }
                }
                if (stackTraceElement.className == "com.saurik.substrate.MS$2" && stackTraceElement.methodName == "invoked") {
                    stackOverflow("HookDetection, A method on the stack trace has been hooked using Substrate.")
                }
                if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "main") {
                    stackOverflow("HookDetection, Xposed is active on the device.")
                }
                if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "handleHookedMethod") {
                    stackOverflow("HookDetection, A method on the stack trace has been hooked using Xposed.")
                }
                try {
                    Thread.sleep(20)
                } catch (ignored: InterruptedException) {
                }
            }
        }

    }

    /*override fun onNavigationItemSelected(item: MenuItem): Boolean {
        navDrawer!!.closeDrawer(GravityCompat.START)
        return when (item.itemId) {
            R.id.payload -> {
                if (isImportFile(this@MainActivity)) {
                    Utils.createAlertDialog(
                        this@MainActivity,
                        getString(R.string.payload),
                        getString(R.string.imported_configuration_active),
                        false,
                        null
                    )
                } else {
                    val vpnEnabled =
                        DmSharedPreferences.getInstance(this@MainActivity)!!.getBoolean(
                            DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
                            DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN_DEFAULT
                        )

                    if (!vpnEnabled) {
                        PayloadGenerator.initPayloadGenerator(
                            this,
                            object : PayloadGenerator.DialogClickListener {
                                override fun onPositiveClickListener(customPayload: String) {
                                    supportFragmentManager.fragments.forEach(fun(item) {
                                        item!!.onActivityResult(
                                            StartFragment.PAYLOAD_GENERATOR_REQUEST_CODE,
                                            Activity.RESULT_OK,
                                            null
                                        )
                                    })
                                }
                            })
                    } else {
                        Utils.createAlertDialog(
                            this@MainActivity,
                            resources.getString(R.string.connection_active),
                            resources.getString(R.string.connection_active_msg),
                            false,
                            null
                        )
                    }
                }
                false
            }
            R.id.policy -> {
                val str = assets?.open("html/privacy.html")?.bufferedReader().use { it?.readText() }
                    ?.replace("[DEV]", "UnknownDev")
                    ?.replace("[APP_NAME]", getString(R.string.app_name))
                    ?.replace("[SUPPORT_EMAIL]", "unknowndev2016@gmail.com")
                //?.replace("[WEBSITE]", getString(R.string.website))
                Utils.createAlertDialog(
                    this@MainActivity,
                    resources.getString(R.string.about_us),
                    Html.fromHtml(str),
                    false,
                    null
                )
                false
            }

            R.id.terms -> {
                val str = assets?.open("html/terms.html")?.bufferedReader().use { it?.readText() }
                    ?.replace("[DEV]", "UnknownDev")
                    ?.replace("[APP_NAME]", getString(R.string.app_name))
                    ?.replace("[SUPPORT_EMAIL]", "unknowndev2016@gmail.com")
                //?.replace("[WEBSITE]", getString(R.string.website))
                Utils.createAlertDialog(
                    this@MainActivity,
                    resources.getString(R.string.about_us),
                    Html.fromHtml(str),
                    false,
                    null
                )
                false
            }

            R.id.license -> {
                val str = assets?.open("licenses.html")?.bufferedReader().use { it?.readText() }
                    ?.replace("[DEV]", "UnknownDev")
                    ?.replace("[APP_NAME]", getString(R.string.app_name))
                    ?.replace("[SUPPORT_EMAIL]", "unknowndev2016@gmail.com")
                //?.replace("[WEBSITE]", getString(R.string.website))
                Utils.createAlertDialog(
                    this@MainActivity,
                    resources.getString(R.string.about_us),
                    Html.fromHtml(str),
                    false,
                    null
                )
                false
            }
            R.id.MoreAPP -> {
                Utils.openPageOnBrowser(
                    this@MainActivity,
                    "https://play.google.com/store/apps/developer?id=UnknownDev%7B%7D"
                )
                false
            }
            R.id.telegram -> {
                Utils.openPageOnBrowser(this@MainActivity, "https://t.me/DiamProxy")
                false
            }
            R.id.feedback -> {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "unknowndev2016@gmail.com", null
                    )
                )
                startActivity(Intent.createChooser(emailIntent, getString(R.string.sendFeedback)))

                false
            }
            R.id.about -> {
                @Suppress("DEPRECATION")
                Utils.createAlertDialog(
                    this@MainActivity,
                    resources.getString(R.string.about_us),
                    Html.fromHtml(Utils.AboutUS),
                    false,
                    null
                )
                false
            }
            R.id.start -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(resources.getString(R.string.dialog_rate_title))
                    .setCancelable(false)
                    .setPositiveButton(
                        resources.getString(R.string.dialog_rate_positive_button)
                    ) { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                        Utils.openAppOnPlayStore(this@MainActivity, "com.nicadevelop.nicavpn")
                    }
                    .setNegativeButton(
                        resources.getString(R.string.dialog_rate_negative_button)
                    ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
                false
            }
            R.id.advance_settings_vpn -> {
                val vpnEnabled =
                    DmSharedPreferences.getInstance(this@MainActivity)!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN_DEFAULT
                    )
                if (!vpnEnabled) {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Utils.createAlertDialog(
                        this@MainActivity,
                        resources.getString(R.string.connection_active),
                        resources.getString(R.string.connection_active_msg),
                        false,
                        null
                    )
                }
                false
            }
            R.id.advance_settings -> {
                val vpnEnabled =
                    DmSharedPreferences.getInstance(this@MainActivity)!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN_DEFAULT
                    )

                if (!vpnEnabled) {
                    Utils.createAdvanceSettingsDialog(
                        this@MainActivity,
                        object : Utils.DialogAdvanceSettingsClickListener {
                            override fun onDialogPositiveClickListener(
                                customDns: String,
                                enableUDP: Boolean,
                                enableServeByIp: Boolean,
                                dialog: AlertDialog
                            ) {
                                DmSharedPreferences.getInstance(this@MainActivity)!!.putString(
                                    DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS,
                                    customDns
                                )
                                DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
                                    DmPrefsKeys.SHARED_PREFERENCES_VPN_UDP,
                                    enableUDP
                                )
                                DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
                                    DmPrefsKeys.SHARED_PREFERENCES_VPN_BY_IP,
                                    enableServeByIp
                                )

                                var server = ""
                                val domain = DmSharedPreferences.getInstance(this@MainActivity)!!
                                    .getString(
                                        DmPrefsKeys.SHARED_PREFERENCES_SERVER,
                                        DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
                                    ).toString()
                                val typeConnection =
                                    DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
                                        DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION,
                                        DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION_DEFAULT
                                    )

// activar debug
*//*                                e(
                                    LogUtil.DIAM_PROXY_TAG,
                                    TAG + "createAdvanceSettingsDialog: typeConnection = $typeConnection"
                                )*//*
                                if (enableServeByIp) {
                                    if (typeConnection == getString(
                                            R.string.http_connection
                                        ) || typeConnection == getString(
                                            R.string.direct_connection
                                        )
                                    ) {
// activar debug
//                                        e(
//                                            LogUtil.DIAM_PROXY_TAG,
//                                            TAG + "createAdvanceSettingsDialog: buscando server ip..."
//                                        )
                                        server = ServerModelArrayList().getServerByDomainName(
                                            domain,
                                            ServerModelArrayList().serverList
                                        )!!.ip
                                    } else if (typeConnection == getString(
                                            R.string.payload_connection
                                        )
                                    ) {
                                        server = ServerModelArrayList().getServerByDomainName(
                                            domain,
                                            ServerModelArrayList().serverPayloadList
                                        )!!.ip
                                    } else if (typeConnection == getString(
                                            R.string.fastdns_connection
                                        )
                                    ) {
                                        server = ServerModelArrayList().getServerByDomainName(
                                            domain,
                                            ServerModelArrayList().serverFastDnsList
                                        )!!.ip
                                    } else {
                                        server = ServerModelArrayList().getServerByDomainName(
                                            domain,
                                            ServerModelArrayList().serverDirectList
                                        )!!.ip
                                    }
                                } else {
                                    if (typeConnection == getString(
                                            R.string.http_connection
                                        ) || typeConnection == getString(
                                            R.string.direct_connection
                                        )
                                    ) {
                                        server = ServerModelArrayList().getServerByIp(
                                            domain,
                                            ServerModelArrayList().serverList
                                        )!!.domainName
                                    } else if (typeConnection == getString(
                                            R.string.payload_connection
                                        )
                                    ) {
                                        server = ServerModelArrayList().getServerByIp(
                                            domain,
                                            ServerModelArrayList().serverPayloadList
                                        )!!.domainName
                                    } else if (typeConnection == getString(
                                            R.string.fastdns_connection
                                        )
                                    ) {
                                        server = ServerModelArrayList().getServerByIp(
                                            domain,
                                            ServerModelArrayList().serverFastDnsList
                                        )!!.domainName
                                    } else {
                                        server = ServerModelArrayList().getServerByIp(
                                            domain,
                                            ServerModelArrayList().serverDirectList
                                        )!!.domainName
                                    }
                                }

                                DmSharedPreferences
                                    .getInstance(this@MainActivity)!!
                                    .putString(
                                        DmPrefsKeys.SHARED_PREFERENCES_SERVER,
                                        server
                                    )
                                dialog.dismiss()
                            }

                        }
                    )
                } else {
                    Utils.createAlertDialog(
                        this@MainActivity,
                        resources.getString(R.string.connection_active),
                        resources.getString(R.string.connection_active_msg),
                        false,
                        null
                    )
                }
                false
            }
            else -> false
        }
    }*/

    private val onPageChangeListener: ViewPager.OnPageChangeListener = object :
        ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        } // nothing needed here

        override fun onPageSelected(position: Int) {
            // AppoDealAdManager.Instance().showinterstitial(this@MainActivity)
        }

        override fun onPageScrollStateChanged(state: Int) {} // nothing needed here
    }

    internal inner class SectionsPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            startFragment =
                StartFragment.newInstance(
                    object :
                        StartFragment.OnFragmentInteractionListener {
                        override fun onFragmentStartInteraction() {
                            startVpn()
                            LogFragment.addLog(this@MainActivity.getString(R.string.sendinjectormethod))
                            Handler().postDelayed({ HTTPtunnelIntentService.start(applicationContext)
                            }, 500)
                        }

                        override fun onFragmentStopInteraction() {
                            LogFragment.addLog(this@MainActivity.getString(R.string.stopinjectormethod))
                            stopTunnelService()
                            stopVpn()
                        }
                    })
            HTTPtunnelIntentService.checkStatus(this@MainActivity)

            if (intentImport != null) {
                startFragment!!.setFileImportedPath(intentImport!!.data!!.path)
            }

            return startFragment!!
        }

        override fun getCount(): Int {
            val res: Resources = resources
            val tabs: Array<String?> = res.getStringArray(R.array.tabs_array)
            return tabs.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            val res: Resources = resources
            val tabs: Array<String> = res.getStringArray(R.array.tabs_array)
            return tabs[position]
        }
    }

    private fun stopTunnelService() {
        LogFragment.addLog(this@MainActivity.getString(R.string.stopTunnelService))
        //LogUtil.v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "stopTunnelService")
        try {
            val intent = Intent(this, HTTPtunnelIntentService::class.java)
            stopService(intent)
        } catch (e: Exception) {
        }
    }

    private fun removeBroadcastWifi() {
        try {
            unregisterReceiver(brcWifiConnected)
        } catch (e: Exception) {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "unregisterReceiver(brcWifiConnected).error")
        }
        if (brcWifiConnected != null) brcWifiConnected = null
    }

    private fun removeBroadcastWifiAp() {
        try {
            unregisterReceiver(brcWifiApStatus)
        } catch (e: Exception) {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "unregisterReceiver(brcWifiConnected).error")
        }
        if (brcWifiApStatus != null) brcWifiApStatus = null
    }

    private fun removeBroadcastVpn() {
        try {
            unregisterReceiver(brcVpnConnected)
        } catch (e: Exception) {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "unregisterReceiver(brcVpnConnected).error")
        }
        if (brcVpnConnected != null) brcVpnConnected = null
    }

    private fun removeBroadcastDeveloperMode() {
        try {
            unregisterReceiver(brcSettingDeveloperMode)
        } catch (e: Exception) {
// activar debug
/*            e(
                LogUtil.DIAM_PROXY_TAG,
                TAG + "unregisterReceiver(brcSettingDeveloperMode).error"
            )*/
        }
        if (brcSettingDeveloperMode != null) brcSettingDeveloperMode = null
    }

    override fun onResume() {
        Log.d("i_am_called000" , "a")
        super.onResume()
        HTTPtunnelIntentService.checkStatus(this)
        if (DmSharedPreferences.getInstance(this)!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_NEW_VERSION,
                DmPrefsKeys.SHARED_PREFERENCES_NEW_VERSION_DEFAULT
            )
        ) {
            stopTunnelService()
            stopService()
            Utils.createAlertDialog(
                this,
                resources.getString(R.string.dialog_new_version_title),
                resources.getString(R.string.dialog_new_version_content),
                true,
                object : Utils.DialogClickListener {
                    override fun onDialogPositiveClickListener() {
                        Utils.openAppOnPlayStore(
                            this@MainActivity,
                            "com.nicadevelop.nicavpn"
                        )
                        finish()
                    }
                }
            )
        }

//        Appodeal.show(this@MainActivity, Appodeal.BANNER_VIEW)
    }

    override fun onDestroy() {
        removeBroadcastWifi()
        removeBroadcastDeveloperMode()
        removeBroadcastVpn()
        removeBroadcastWifiAp()
        checkHooking()
        checkVPNStatus()
        super.onDestroy()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder) {
        e(LogUtil.DIAM_PROXY_TAG + "V2RAY", "MainActivity.onServiceConnected")
        needUnbind = true
        val controller: ServerClientService.Controller = service as ServerClientService.Controller
        serviceControl = controller.control
        server = serviceControl!!.clientServer
        if (server == null) {
            stopVpn()
            val serverPort = DmSharedPreferences
                .getInstance(this@MainActivity)!!
                .getInt(
                    DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT,
                    DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT_DEFAULT.toInt()
                )
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: serverPort->$serverPort")

            val serverAddress = DmSharedPreferences
                .getInstance(this@MainActivity)!!
                .getString(
                    DmPrefsKeys.SHARED_PREFERENCES_SERVER,
                    DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
                )
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: serverAddress->$serverAddress")

            val payloadConfig = DmSharedPreferences
                .getInstance(this@MainActivity)!!
                .getString(
                    DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG,
                    DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG_DEFAULT
                )
            // Frida detect
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: PayloadConfig->$payloadConfig")

            serviceControl!!.setClientServer(serverAddress, serverPort, payloadConfig!!)
            server = serviceControl!!.clientServer

            Handler().postDelayed({
                startService()
            }, 500)
            // startService()
        }
        startVpn()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        e(LogUtil.DIAM_PROXY_TAG + "V2RAY", "MainActivity.onServiceDisconnected")
        stopVpn()
        if (server != null) server!!.close()
        server = null
        serviceControl = null
        stopService(Intent(this, ServerClientService::class.java))
    }

    private fun unbindService() {
        if (needUnbind) {
            needUnbind = false
            unbindService(this)
        }
    }

    fun startBindService() {
        bindService(
            Intent(this@MainActivity, ServerClientService::class.java),
            this@MainActivity,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun startService() {
        DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
            true
        )

        val i = Intent(this, ServerClientService::class.java)
        startService(i)
    }

    fun stopService() {
        DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
            false
        )

        e(LogUtil.DIAM_PROXY_TAG + "V2RAY", "MainActivity.stopService")
        stopVpn()
        unbindService()
        stopPinger()
        serviceControl = null
        try {
            val i = Intent(this, ServerClientService::class.java)
            stopService(i)
        } catch (e: Exception) {
        }
    }

    /**
     * Pinger
     */
    var pingHandler: Handler? = null
    private val checkPingTask = object : Runnable {
        override fun run() {
            try {
                Utils.getLatency(
                    this@MainActivity,
                    DmSharedPreferences
                        .getInstance(this@MainActivity)!!
                        .getString(
                            DmPrefsKeys.SHARED_PREFERENCES_SERVER,
                            DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
                        ).toString()
                )
            } catch (e: java.lang.Exception) {
                //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.ping error", e)
            }
            pingHandler!!.postDelayed(this, 40 * 1000)
        }
    }

    @Throws(java.lang.Exception::class)
    private fun startPinger() {
        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.starting pinger")
        stopPinger()
        pingHandler = Handler(Looper.getMainLooper())
        pingHandler!!.postDelayed(checkPingTask, 0)
    }

    @Synchronized
    private fun stopPinger() {
        try {
            if (pingHandler != null) {
                pingHandler!!.removeCallbacks(checkPingTask)
            }
        } catch (e: java.lang.Exception) {
            //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.stopPinger: $e")
        }
    }

    private fun isImportFile(context: Context): Boolean {
        return DmSharedPreferences.getInstance(context)!!.getBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF,
            DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF_DEFAULT
        )
    }

    companion object {
        const val ACTION_MAIN_WIFI_CONNECTED = "com.sock.diam.proxy.ACTION_MAIN_BCR_WIFI_CONNECTED"
        const val ACTION_MAIN_DEVELOPER_SETTINGS =
            "com.sock.diam.proxy.ACTION_MAIN_BCR_DEVELOPER_SETTINGS"
        const val DEBUGGER_CONNECTED =
            "com.sock.diam.proxy.DEBUGGER_CONNECTED"
        const val CHANNEL_ID = "NOTIFY_CHANNEL_1"
        const val FILE_REQUEST_CODE = 9000
    }
}


//@file:Suppress("ConstantConditionIf")
//
//package com.nicadevelop.nicavpn
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.*
//import android.content.res.Resources
//import android.net.ConnectivityManager
//import android.net.NetworkCapabilities
//import android.net.VpnService
//import android.os.*
//import android.os.StrictMode.VmPolicy
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import android.widget.LinearLayout
//import android.widget.RelativeLayout
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentPagerAdapter
//import androidx.lifecycle.Observer
//import androidx.viewpager.widget.ViewPager
//import com.github.florent37.runtimepermission.kotlin.askPermission
//import com.google.android.material.button.MaterialButton
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.textfield.TextInputLayout
//import com.nicadevelop.nicavpn.http.request.ClientServer
//import com.nicadevelop.nicavpn.http.request.ServiceControl
//import com.nicadevelop.nicavpn.models.Profile
//import com.nicadevelop.nicavpn.service.*
//import com.nicadevelop.nicavpn.tools.LogUtil
//import com.nicadevelop.nicavpn.tools.LogUtil.e
//import com.nicadevelop.nicavpn.tools.Utils
//import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsKeys
//import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsMethods
//import com.nicadevelop.nicavpn.tools.sharedPreferences.DmSharedPreferences
//import com.nicadevelop.nicavpn.vpn.util.ProfileManager
//import com.nicadevelop.nicavpn.vpn.util.Utility
//import com.sdsmdg.tastytoast.TastyToast
//import com.tencent.mmkv.MMKV
//import com.v2ray.ang.AppConfig
//import com.v2ray.ang.service.V2RayServiceManager
//import com.v2ray.ang.util.MmkvManager
//import com.v2ray.ang.util.Utils.stopVService
//import com.v2ray.ang.viewmodel.MainViewModel
//import io.michaelrocks.paranoid.Obfuscate
//import kotlinx.android.synthetic.main.layout_dns_tt.*
//import kotlinx.android.synthetic.main.layout_edit_text_dialog.*
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.*
//import kotlin.system.exitProcess
//
//
//@Obfuscate
//class MainActivity : AppCompatActivity(), ServiceConnection {
//    private val TAG = MainActivity::class.java.simpleName + "."
//
//    /**
//     * ===============================================================
//     * Socks 5
//     * ===============================================================
//     */
//    ///region 'Socks 5'
//
//    private var mManager: ProfileManager? = null
//    private var mProfile: Profile? = null
//    private var mBinder: IVpnService? = null
//    private var mRunning = false
//    private var mStarting = false
//    private var mStopping = false
//    private val mConnection = object : ServiceConnection {
//        override fun onServiceConnected(p1: ComponentName, binder: IBinder) {
//            mBinder = IVpnService.Stub.asInterface(binder)
//
//            try {
//                mRunning = mBinder!!.isRunning
//            } catch (e: Exception) {
//            }
//
//            if (mRunning) {
//                updateState()
//            }
//        }
//
//        override fun onServiceDisconnected(p1: ComponentName) {
//            mBinder = null
//        }
//    }
//    ///endregion
//    /**
//     * ===============================================================
//     * Fin Socks 5
//     * ===============================================================
//     */
//    private var brcWifiConnected: BroadcastReceiver? = null
//    private var brcWifiApStatus: BroadcastReceiver? = null
//    private var brcVpnConnected: BroadcastReceiver? = null
//    private var brcOSAnsADS: BroadcastReceiver? = null
//    private var brcSettingDeveloperMode: BroadcastReceiver? = null
//    private var navDrawer: DrawerLayout? = null
//    private var men_btn_click: LinearLayout? = null
//    private var needUnbind = false
//    private var serviceControl: ServiceControl? = null
//    var server: ClientServer? = null
//    var btnStartVpnMode: MaterialButton? = null
//    var intentImport: Intent? = null
//    var startFragment: StartFragment? = null
//
//    private val mainStorage by lazy {
//        MMKV.mmkvWithID(
//            MmkvManager.ID_MAIN,
//            MMKV.MULTI_PROCESS_MODE
//        )
//    }
//
//    private val settingsStorage by lazy {
//        MMKV.mmkvWithID(
//            MmkvManager.ID_SETTING,
//            MMKV.MULTI_PROCESS_MODE
//        )
//    }
//
//    private val requestVpnPermission =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == RESULT_OK) {
//                // startV2Ray()
//                TastyToast.makeText(
//                    this@MainActivity,
//                    getString(R.string.reconnect),
//                    Toast.LENGTH_LONG,
//                    TastyToast.ERROR
//                ).show()
//            }
//        }
//    val mainViewModel: MainViewModel by viewModels()
//
//    private fun stackOverflow(error: String) {
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(1)
//        val sendIntent = Intent()
//        this.startActivity(sendIntent)
//        exitProcess(0)
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        // Frida detect
//        System.loadLibrary(Constants.LibUltima)
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        /* val consent = if (savedInstanceState == null) {
//             intent.getBooleanExtra("concent", false)
//         } else {
//             val consentStatus: Consent.Status = ConsentManager.getInstance(this).consentStatus
//             (consentStatus === Consent.Status.PERSONALIZED
//                     || consentStatus === Consent.Status.PARTLY_PERSONALIZED)
//         }
//
//         Appodeal.setTesting(BuildConfig.DEBUG)
//         Appodeal.setLogLevel(Log.LogLevel.debug)
//         Appodeal.setAutoCache(Appodeal.BANNER_VIEW, true)
//         Appodeal.setAutoCache(Appodeal.INTERSTITIAL, true)
//         Appodeal.initialize(this, AppoDealAdManager.APP_KEY, Appodeal.INTERSTITIAL, consent)
//         Appodeal.initialize(this, AppoDealAdManager.APP_KEY, Appodeal.BANNER_VIEW, consent)
//         Appodeal.setBannerViewId(R.id.appodealBannerView)*/
//
//        /**
//         * Generador de llave AES.
//         * la llave generada se motrara en el log de la siguiente forma:
//         * Utils..generateSecretKey: pxSjrbpEiqDOMxiYe3VcWQ==
//         */
//        //Utils.generateSecretKey()
//
//
//        /**
//         * ===============================================================
//         * Socks 5
//         * ===============================================================
//         */
//        ///region 'Socks 5'
//        mManager = ProfileManager.getInstance(this)
//        if (mProfile == null) {
//            mProfile = mManager!!.default
//        }
//        ///endregion
//        /**
//         * ===============================================================
//         * Fin Socks 5
//         * ===============================================================
//         */
//        HTTProcessManager.setupConfig(this)
//
////        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        val mViewPager: NonSwipeableViewPager = findViewById(R.id.container)
//        navDrawer = findViewById(R.id.drawer_layout)
//        men_btn_click = findViewById(R.id.men_btn_click)
//
//        val tabLayout: TabLayout = findViewById(R.id.tabs)
////        val navigationView: NavigationView = findViewById(R.id.navigationView)
////        setSupportActionBar(toolbar)
////        if (supportActionBar != null && !BuildConfig.IS_VPN_MODE) {
////            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
////            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
////        }
//
////        Appodeal.show(this@MainActivity, Appodeal.BANNER_VIEW)
//
//        val mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
//        mViewPager.adapter = mSectionsPagerAdapter
//        tabLayout.setupWithViewPager(mViewPager)
//        mViewPager.addOnPageChangeListener(onPageChangeListener)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name: CharSequence = getString(R.string.notification_channel)
//            val description: String = getString(R.string.notification_desc)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance)
//            channel.description = description
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            channel.enableVibration(false)
//
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//        removeBroadcastVpn()
//        val filterVpn = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
//        brcVpnConnected = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val extras = intent!!.extras
//                if (extras != null) {
//                    LogUtil.i(
//                        LogUtil.DIAM_PROXY_TAG,
//                        "mIRNetwork: ACTION_BACKGROUND_DATA_SETTING_CHANGED: " + extras.getString(
//                            ConnectivityManager.ACTION_BACKGROUND_DATA_SETTING_CHANGED
//                        )
//                    )
//                    LogUtil.i(
//                        LogUtil.DIAM_PROXY_TAG,
//                        "mIRNetwork: CONNECTIVITY_ACTION: " + extras.getString(ConnectivityManager.CONNECTIVITY_ACTION)
//                    )
//                    LogFragment.addLog("APN: " + extras.getString(ConnectivityManager.EXTRA_EXTRA_INFO))
//                    LogUtil.i(
//                        LogUtil.DIAM_PROXY_TAG,
//                        "mIRNetwork: EXTRA_EXTRA_INFO: " + extras.getString(ConnectivityManager.EXTRA_EXTRA_INFO)
//                    )
//                    LogUtil.i(
//                        LogUtil.DIAM_PROXY_TAG,
//                        "mIRNetwork: EXTRA_NO_CONNECTIVITY: " + extras.getString(ConnectivityManager.EXTRA_NO_CONNECTIVITY)
//                    )
//                    LogUtil.i(
//                        LogUtil.DIAM_PROXY_TAG,
//                        "mIRNetwork: EXTRA_REASON: " + extras.getString(ConnectivityManager.EXTRA_REASON)
//                    )
//
//                    if (extras.getString(ConnectivityManager.EXTRA_REASON) != null
//                        && extras.getString(ConnectivityManager.EXTRA_REASON) == "agentDisconnect"
//                    ) {
//                        stopVpnProcess()
//                    }
//                }
//                if (checkVPNStatus()) {
//                    stopVpnProcess()
//                }
//            }
//        }
//        registerReceiver(brcVpnConnected, filterVpn)
//
////        navigationView.setNavigationItemSelectedListener(this)
//
//     /*   HTTPtunnelIntentService.isRunning.observe(this, Observer { aBoolean: Boolean ->
//            if (aBoolean) {
//                LogFragment.addLog(this@MainActivity.getString(R.string.IntentServiceRUN))
//                startVpn()
//            } else {
//                LogFragment.addLog(this@MainActivity.getString(R.string.IntentServiceSTOP))
//                stopVpn()
//            }
//        })*/
//
//        val mainVpnMode: RelativeLayout = findViewById(R.id.main_vpn_mode)
//        btnStartVpnMode = findViewById(R.id.btnStartVpnMode)
//        @Suppress("ConstantConditionIf")
//        if (BuildConfig.IS_VPN_MODE) {
//            mainVpnMode.visibility = View.VISIBLE
//            mViewPager.visibility = View.GONE
////            navigationView.visibility = View.GONE
//            tabLayout.visibility = View.GONE
//            btnStartVpnMode!!.visibility = View.VISIBLE
//        }
//
//        men_btn_click!!.setOnClickListener {
//            val intent = Intent(this@MainActivity, Menu_Activity::class.java)
//            startActivity(intent)
//        }
//
//        btnStartVpnMode!!.setOnClickListener {
//            /*if (btnStartVpnMode!!.text == getString(R.string.payload_start)) {
//                startVpn()
//            } else {
//                stopVpn()
//                btnStartVpnMode!!.text = getString(R.string.payload_start)
//            }*/
//        }
//
//        val intent = intent
//        val scheme = intent.scheme
//        if (scheme != null && (scheme == "file" || scheme == "content")) {
//            val data = intent.data
//            val file = File(data!!.path)
//
//            if (!file.absolutePath.contains(Constants.EXT_FILE)) {
//                TastyToast.makeText(
//                    this,
//                    getString(R.string.file_invalid),
//                    Toast.LENGTH_LONG,
//                    TastyToast.ERROR
//                ).show()
//            } else {
//                intentImport = intent
//            }
//        }
//
//        setupViewModelObserver()
//
//        removeBroadcastVerifyOSAndADS()
//        val osADSFilter = IntentFilter()
//        osADSFilter.addAction("com.nicadevelop.nicavpn.OSADS")
//        brcOSAnsADS = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
////                Appodeal.show(this@MainActivity, Appodeal.INTERSTITIAL)
//            }
//        }
//        registerReceiver(brcOSAnsADS, osADSFilter)
//
//        if (BuildConfig.BUILD_TYPE.contentEquals("debug")) {
//            StrictMode.setVmPolicy(
//                VmPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build()
//            )
//        }
//    }
//
//    private fun setupViewModelObserver() {
//        mainViewModel.updateListAction.observe(this) {
//            val index = it ?: return@observe
//            if (index >= 0) {
//                //adapter.notifyItemChanged(index)
//            } else {
//                //adapter.notifyDataSetChanged()
//            }
//        }
//
//        mainViewModel.isRunning.observe(this) {
//            val isRunning = it ?: return@observe
//            //adapter.isRunning = isRunning
//            if (isRunning) {
//
//            } else {
//
//            }
//        }
//        mainViewModel.startListenBroadcast()
//    }
//
//    private fun stopVpnProcess() {
//        stopService()
//        stopTunnelService()
//        if (startFragment != null) {
//            startFragment!!.stopPayloadProcess()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        /*if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            supportFragmentManager.fragments.forEach(fun(item) {
//                item!!.onActivityResult(requestCode, resultCode, data)
//            })
//            return
//        }*/
//        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            data?.data?.also { documentUri ->
//                contentResolver.takePersistableUriPermission(
//                    documentUri,
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION
//                )
//                supportFragmentManager.fragments.forEach(fun(item) {
//                    item!!.onActivityResult(requestCode, resultCode, data)
//                })
//            }
//            return
//        }
//        /**
//         * ===============================================================
//         * Socks 5
//         * ===============================================================
//         */
//        ///region 'Socks 5'
//        if (resultCode == RESULT_OK) {
////            Utility.startVpn(this, mProfile!!)
////            checkState()
////            btnStartVpnMode!!.text = getString(R.string.payload_stop)
//        }
//        ///endregion
//        /**
//         * ===============================================================
//         * Fin Socks 5
//         * ===============================================================
//         */
//
//    }
//
//    /**
//     * ===============================================================
//     * Socks 5
//     * ===============================================================
//     */
//    ///region 'Socks 5'
//    private fun updateState() {
//        if (mBinder == null) {
//            mRunning = false
//        } else {
//            try {
//                mRunning = mBinder!!.isRunning
//            } catch (e: Exception) {
//                mRunning = false
//            }
//
//        }
//
//
//        @Suppress("ControlFlowWithEmptyBody")
//        if (!mStarting && !mStopping || mStarting && mRunning || mStopping && !mRunning) {
//            // mSwitch!!.isEnabled = true
//        }
//
//        if (mStarting && mRunning) {
//            mStarting = false
//        }
//
//        if (mStopping && !mRunning) {
//            mStopping = false
//        }
//    }
//
//    public fun startVpn() {
//
//        mStarting = true
//        //val i = VpnService.prepare(this)
//        LogFragment.addLog(this@MainActivity.getString(R.string.Startvpn))
//
//
//        val intent = VpnService.prepare(this)
//        if (intent == null) {
//
//            startV2Ray()
//        } else {
//            requestVpnPermission.launch(intent)
//        }
////        Appodeal.show(this@MainActivity, Appodeal.INTERSTITIAL)
//    }
//
//    fun startV2Ray() {
//        V2RayServiceManager.startV2Ray(this@MainActivity)
//
//    }
//
//
//    public fun stopVpn2() {
//
//        com.v2ray.ang.service.V2RayVpnService.isRunning = true
//        mRunning = true
//        V2RayServiceManager.v2rayPoint.isRunning = true
//
//
//        if(com.v2ray.ang.service.V2RayVpnService.isRunning){
//            TastyToast.makeText(
//                this,
//                "isrunning true",
//                Toast.LENGTH_LONG,
//                TastyToast.ERROR
//            ).show()
//        }else{
//            TastyToast.makeText(
//                this,
//                "isrunning false",
//                Toast.LENGTH_LONG,
//                TastyToast.ERROR
//            ).show()
//        }
//
//
//        if (V2RayServiceManager.v2rayPoint.isRunning){
//            TastyToast.makeText(
//                this,
//                "vpoin true",
//                Toast.LENGTH_LONG,
//                TastyToast.ERROR
//            ).show()
//        }else{
//            TastyToast.makeText(
//                this,
//                "vpoin false",
//                Toast.LENGTH_LONG,
//                TastyToast.ERROR
//            ).show()
//        }
//
//    /*    mRunning = true
//
//        if(mRunning){
//            TastyToast.makeText(
//                this,
//                "true",
//                Toast.LENGTH_LONG,
//                TastyToast.ERROR
//            ).show()
//        }else{
//            TastyToast.makeText(
//                this,
//                "false",
//                Toast.LENGTH_LONG,
//                TastyToast.ERROR
//            ).show()
//        }*/
//
//    }
//
//
//
//    public fun stopVpn() {
//        LogFragment.addLog(getString(R.string.Stopvpn))
//
//
//
//
//
//
//        stopVService(this@MainActivity)
//        if (mBinder == null)
//            return
//
//        mStopping = true
//
//        try {
//           mBinder!!.stop()
//        } catch (e: Exception) {
//
//        }
//
//        mBinder = null
//
//        LogFragment.addLog(this@MainActivity.getString(R.string.Stopvpn))
//        unbindService(mConnection)
//        checkState()
//    }
//
//    private fun checkState() {
//        mRunning = false
//
//        if (mBinder == null) {
//            bindService(Intent(this, DmVpnService::class.java), mConnection, 0)
//        }
//    }
//    ///endregion
//    /**
//     * ===============================================================
//     * Fin Socks 5
//     * ===============================================================
//     */
//
//    private fun removeBroadcastVerifyOSAndADS() {
//        try {
//            unregisterReceiver(brcOSAnsADS)
//        } catch (e: Exception) {
//        }
//        if (brcOSAnsADS != null) brcOSAnsADS = null
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        if (!BuildConfig.IS_VPN_MODE) {
//            menuInflater.inflate(R.menu.menu_main, menu)
//        }
//        return true
//    }
//
//    @OptIn(ExperimentalStdlibApi::class)
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                if (!navDrawer!!.isDrawerOpen(GravityCompat.START)) {
//                    navDrawer!!.openDrawer(GravityCompat.START)
//                } else {
//                    navDrawer!!.closeDrawer(GravityCompat.END)
//                }
//                true
//            }
//            R.id.mImport -> {
//                openDocumentPicker()
//                true
//            }
//            R.id.mExport -> {
//                //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected: Export")
//                if (DmSharedPreferences.getInstance(this@MainActivity)!!
//                        .getBoolean(DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF, false)
//                ) {
//                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: Configuration Imported....")
//                    /**
//                     * Cambiar el mensaje mostrado con el formato getString(R.string.files_not_ready)
//                     */
//                    TastyToast.makeText(
//                        this@MainActivity,
//                        getString(R.string.allowedexiste), Toast.LENGTH_LONG, TastyToast.ERROR
//                    ).show()
//                    return true
//                }
//                if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
//                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: File not available")
//                    TastyToast.makeText(
//                        this@MainActivity,
//                        getString(R.string.files_not_ready), Toast.LENGTH_LONG, TastyToast.WARNING
//                    ).show()
//                } else {
//                    askPermission(
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ) {
//                        Utils.createEditTextDialog(
//                            this@MainActivity,
//                            object : Utils.DialogTextClickListener {
//                                override fun onDialogPositiveClickListener(
//                                    fileName: String,
//                                    fileMessage: String,
//                                    dialog: AlertDialog
//                                ) {
//                                    if (fileName.isEmpty()) {
//                                        val textError: TextInputLayout =
//                                            dialog.input_layout_file as TextInputLayout
//                                        textError.error =
//                                            this@MainActivity.getString(R.string.enter_valid_file_name)
//                                        return
//                                    }
//                                    DmSharedPreferences.getInstance(this@MainActivity)!!
//                                        .putString(
//                                            DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG,
//                                            fileMessage
//                                        )
//
//                                    val dir =
//                                        Utils.getFileDirectory().path + File.separator + fileName + Constants.EXT_FILE
//                                    //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.Export.File: $dir")
//                                    val file = File(dir)
//                                    if (file.exists()) {
//                                        Utils.createAlertDialog(
//                                            this@MainActivity,
//                                            getString(R.string.action_export),
//                                            getString(R.string.the_file_already_exists),
//                                            true,
//                                            null,
//                                            object : Utils.DialogClickListener {
//                                                override fun onDialogPositiveClickListener() {
//                                                    saveExportFile(fileName)
//                                                    dialog.dismiss()
//                                                }
//                                            },
//                                            true
//                                        )
//                                    } else {
//                                        saveExportFile(fileName)
//                                        dialog.dismiss()
//                                    }
//                                }
//                            })
//                    }.onDeclined { error ->
//                        if (error.hasDenied()) {
//                            TastyToast.makeText(
//                                this@MainActivity,
//                                getString(R.string.permission_denied),
//                                Toast.LENGTH_LONG,
//                                TastyToast.WARNING
//                            ).show()
//                        }
//                        if (error.hasForeverDenied()) {
//                        }
//                    }
//                }
//                true
//            }
//            R.id.mRestore -> {
//                cacheDir.deleteRecursively()
//                clearPreferences()
//                //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected: Restore")
//                true
//            }
//            R.id.mDeleteLog -> {
//                //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected: Delete Log")
////                logFragment!!.clear()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    private fun openDocumentPicker() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            type = "application/*"
//            addCategory(Intent.CATEGORY_OPENABLE)
//        }
//        startActivityForResult(intent, FILE_REQUEST_CODE)
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    @ExperimentalStdlibApi
//    private fun saveExportFile(fileName: String) {
//        val sdf = SimpleDateFormat("ddMyyyyhh:mm:ss")
//        val currentDate = sdf.format(Date())
//        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected.fileId: $fileId")
//
//        val typeConfig =
//            DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
//                DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION,
//                DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION_DEFAULT
//            )
//
//        val server = DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
//            DmPrefsKeys.SHARED_PREFERENCES_SERVER,
//            DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
//        )
//
//        val port = DmSharedPreferences.getInstance(this@MainActivity)!!.getLong(
//            DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT,
//            DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT_DEFAULT
//        )
//
//        val host = DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
//            DmPrefsKeys.SHARED_PREFERENCES_SNI,
//            DmPrefsKeys.SHARED_PREFERENCES_SNI_DEFAULT
//        )
//
//        val dns = DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
//            DmPrefsKeys.SHARED_PREFERENCES_DNS,
//            DmPrefsKeys.SHARED_PREFERENCES_DNS_DEFAULT
//        )
//
//        val useCustomPing =
//            DmSharedPreferences.getInstance(this@MainActivity)!!.getBoolean(
//                DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING,
//                DmPrefsKeys.SHARED_PREFERENCES_CUSTOM_PING_DEFAULT
//            )
//
//        val ping = DmSharedPreferences.getInstance(this@MainActivity)!!.getLong(
//            DmPrefsKeys.SHARED_PREFERENCES_PING,
//            DmPrefsKeys.SHARED_PREFERENCES_PING_DEFAULT
//        )
//
//        val payload = DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
//            DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG,
//            DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG_DEFAULT
//        )
//
//        val customMessage = DmSharedPreferences.getInstance(this@MainActivity)!!
//            .getString(
//                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG,
//                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG_DEFAULT
//            )
//
//        val blockServer = DmSharedPreferences.getInstance(this@MainActivity)!!
//            .getBoolean(DmPrefsKeys.SHARED_PREFERENCES_BLOCK_CONF_SERVER, false)
//
//        val dnsTTHost = DmPrefsMethods.getDnsTTHost()
//        val connDnsTTHost = DmPrefsMethods.getConnectionDnsTT()
//
//        // encryptado shared preferencs
//        val message = Utils.encryptValue(typeConfig) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(server) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(port) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(host) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(dns) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(useCustomPing) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(ping) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(payload) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(customMessage) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(blockServer) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(dnsTTHost) + Constants.FILE_SEPARATOR +
//                Utils.encryptValue(connDnsTTHost) + Constants.FILE_SEPARATOR
//
//        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.onOptionsItemSelected.message: $message")
//        val encryptedValue = Utils.encryptValue(message)
//// activar debug
//
///*        e(
//            LogUtil.DIAM_PROXY_TAG,
//            "$TAG.onOptionsItemSelected.encryptedValue: $encryptedValue"
//        )*/
//
//        var messageResponse = getString(R.string.data_export_error)
//        if (Utils.saveExternalFile(fileName, encryptedValue)) {
//            messageResponse = getString(R.string.data_export_successful)
//        }
//
//        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Toast.makeText(this@MainActivity, "Configuraciones exportadas exitosas en Documents !!", Toast.LENGTH_LONG).show()
//        }else {
//            Toast.makeText(this@MainActivity, messageResponse, Toast.LENGTH_LONG).show()
//        }*/
//        TastyToast.makeText(
//            this@MainActivity,
//            messageResponse,
//            Toast.LENGTH_LONG,
//            TastyToast.WARNING
//        ).show()
//    }
//
//    private fun clearPreferences() {
//        DmSharedPreferences.getInstance(this@MainActivity)!!.clear()
//        DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
//            DmPrefsKeys.SHARED_PREFERENCES_FIRST_INSTALLATION,
//            false
//        )
//        finish()
//        overridePendingTransition(0, 0)
//        startActivity(intent)
//        overridePendingTransition(0, 0)
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun checkVPNStatus(): Boolean {
//        val connectivityManager =
//            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val network = connectivityManager.activeNetwork
//            val capabilities = connectivityManager.getNetworkCapabilities(network)
//            capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
//        } else {
//            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_VPN)!!.isConnectedOrConnecting
//        }
//    }
//
//    private fun checkHooking() {
//        try {
//            throw Exception("blah")
//        } catch (e: Exception) {
//            var zygoteInitCallCount = 0
//            for (stackTraceElement in e.stackTrace) {
//                if (stackTraceElement.className == "com.android.internal.os.ZygoteInit") {
//                    zygoteInitCallCount++
//                    if (zygoteInitCallCount == 2) {
//                        stackOverflow("HookDetection,Substrate is active on the device.")
//                    }
//                }
//                if (stackTraceElement.className == "com.saurik.substrate.MS$2" && stackTraceElement.methodName == "invoked") {
//                    stackOverflow("HookDetection, A method on the stack trace has been hooked using Substrate.")
//                }
//                if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "main") {
//                    stackOverflow("HookDetection, Xposed is active on the device.")
//                }
//                if (stackTraceElement.className == "de.robv.android.xposed.XposedBridge" && stackTraceElement.methodName == "handleHookedMethod") {
//                    stackOverflow("HookDetection, A method on the stack trace has been hooked using Xposed.")
//                }
//                try {
//                    Thread.sleep(20)
//                } catch (ignored: InterruptedException) {
//                }
//            }
//        }
//
//    }
//
//    /*override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        navDrawer!!.closeDrawer(GravityCompat.START)
//        return when (item.itemId) {
//            R.id.payload -> {
//                if (isImportFile(this@MainActivity)) {
//                    Utils.createAlertDialog(
//                        this@MainActivity,
//                        getString(R.string.payload),
//                        getString(R.string.imported_configuration_active),
//                        false,
//                        null
//                    )
//                } else {
//                    val vpnEnabled =
//                        DmSharedPreferences.getInstance(this@MainActivity)!!.getBoolean(
//                            DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
//                            DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN_DEFAULT
//                        )
//
//                    if (!vpnEnabled) {
//                        PayloadGenerator.initPayloadGenerator(
//                            this,
//                            object : PayloadGenerator.DialogClickListener {
//                                override fun onPositiveClickListener(customPayload: String) {
//                                    supportFragmentManager.fragments.forEach(fun(item) {
//                                        item!!.onActivityResult(
//                                            StartFragment.PAYLOAD_GENERATOR_REQUEST_CODE,
//                                            Activity.RESULT_OK,
//                                            null
//                                        )
//                                    })
//                                }
//                            })
//                    } else {
//                        Utils.createAlertDialog(
//                            this@MainActivity,
//                            resources.getString(R.string.connection_active),
//                            resources.getString(R.string.connection_active_msg),
//                            false,
//                            null
//                        )
//                    }
//                }
//                false
//            }
//            R.id.policy -> {
//                val str = assets?.open("html/privacy.html")?.bufferedReader().use { it?.readText() }
//                    ?.replace("[DEV]", "UnknownDev")
//                    ?.replace("[APP_NAME]", getString(R.string.app_name))
//                    ?.replace("[SUPPORT_EMAIL]", "unknowndev2016@gmail.com")
//                //?.replace("[WEBSITE]", getString(R.string.website))
//                Utils.createAlertDialog(
//                    this@MainActivity,
//                    resources.getString(R.string.about_us),
//                    Html.fromHtml(str),
//                    false,
//                    null
//                )
//                false
//            }
//
//            R.id.terms -> {
//                val str = assets?.open("html/terms.html")?.bufferedReader().use { it?.readText() }
//                    ?.replace("[DEV]", "UnknownDev")
//                    ?.replace("[APP_NAME]", getString(R.string.app_name))
//                    ?.replace("[SUPPORT_EMAIL]", "unknowndev2016@gmail.com")
//                //?.replace("[WEBSITE]", getString(R.string.website))
//                Utils.createAlertDialog(
//                    this@MainActivity,
//                    resources.getString(R.string.about_us),
//                    Html.fromHtml(str),
//                    false,
//                    null
//                )
//                false
//            }
//
//            R.id.license -> {
//                val str = assets?.open("licenses.html")?.bufferedReader().use { it?.readText() }
//                    ?.replace("[DEV]", "UnknownDev")
//                    ?.replace("[APP_NAME]", getString(R.string.app_name))
//                    ?.replace("[SUPPORT_EMAIL]", "unknowndev2016@gmail.com")
//                //?.replace("[WEBSITE]", getString(R.string.website))
//                Utils.createAlertDialog(
//                    this@MainActivity,
//                    resources.getString(R.string.about_us),
//                    Html.fromHtml(str),
//                    false,
//                    null
//                )
//                false
//            }
//            R.id.MoreAPP -> {
//                Utils.openPageOnBrowser(
//                    this@MainActivity,
//                    "https://play.google.com/store/apps/developer?id=UnknownDev%7B%7D"
//                )
//                false
//            }
//            R.id.telegram -> {
//                Utils.openPageOnBrowser(this@MainActivity, "https://t.me/DiamProxy")
//                false
//            }
//            R.id.feedback -> {
//                val emailIntent = Intent(
//                    Intent.ACTION_SENDTO, Uri.fromParts(
//                        "mailto", "unknowndev2016@gmail.com", null
//                    )
//                )
//                startActivity(Intent.createChooser(emailIntent, getString(R.string.sendFeedback)))
//
//                false
//            }
//            R.id.about -> {
//                @Suppress("DEPRECATION")
//                Utils.createAlertDialog(
//                    this@MainActivity,
//                    resources.getString(R.string.about_us),
//                    Html.fromHtml(Utils.AboutUS),
//                    false,
//                    null
//                )
//                false
//            }
//            R.id.start -> {
//                val builder = AlertDialog.Builder(this)
//                builder.setMessage(resources.getString(R.string.dialog_rate_title))
//                    .setCancelable(false)
//                    .setPositiveButton(
//                        resources.getString(R.string.dialog_rate_positive_button)
//                    ) { dialog: DialogInterface, _: Int ->
//                        dialog.cancel()
//                        Utils.openAppOnPlayStore(this@MainActivity, "com.nicadevelop.nicavpn")
//                    }
//                    .setNegativeButton(
//                        resources.getString(R.string.dialog_rate_negative_button)
//                    ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
//                val alert = builder.create()
//                alert.show()
//                false
//            }
//            R.id.advance_settings_vpn -> {
//                val vpnEnabled =
//                    DmSharedPreferences.getInstance(this@MainActivity)!!.getBoolean(
//                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
//                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN_DEFAULT
//                    )
//                if (!vpnEnabled) {
//                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else {
//                    Utils.createAlertDialog(
//                        this@MainActivity,
//                        resources.getString(R.string.connection_active),
//                        resources.getString(R.string.connection_active_msg),
//                        false,
//                        null
//                    )
//                }
//                false
//            }
//            R.id.advance_settings -> {
//                val vpnEnabled =
//                    DmSharedPreferences.getInstance(this@MainActivity)!!.getBoolean(
//                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
//                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN_DEFAULT
//                    )
//
//                if (!vpnEnabled) {
//                    Utils.createAdvanceSettingsDialog(
//                        this@MainActivity,
//                        object : Utils.DialogAdvanceSettingsClickListener {
//                            override fun onDialogPositiveClickListener(
//                                customDns: String,
//                                enableUDP: Boolean,
//                                enableServeByIp: Boolean,
//                                dialog: AlertDialog
//                            ) {
//                                DmSharedPreferences.getInstance(this@MainActivity)!!.putString(
//                                    DmPrefsKeys.SHARED_PREFERENCES_VPN_DNS,
//                                    customDns
//                                )
//                                DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
//                                    DmPrefsKeys.SHARED_PREFERENCES_VPN_UDP,
//                                    enableUDP
//                                )
//                                DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
//                                    DmPrefsKeys.SHARED_PREFERENCES_VPN_BY_IP,
//                                    enableServeByIp
//                                )
//
//                                var server = ""
//                                val domain = DmSharedPreferences.getInstance(this@MainActivity)!!
//                                    .getString(
//                                        DmPrefsKeys.SHARED_PREFERENCES_SERVER,
//                                        DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
//                                    ).toString()
//                                val typeConnection =
//                                    DmSharedPreferences.getInstance(this@MainActivity)!!.getString(
//                                        DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION,
//                                        DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION_DEFAULT
//                                    )
//
//// activar debug
//*//*                                e(
//                                    LogUtil.DIAM_PROXY_TAG,
//                                    TAG + "createAdvanceSettingsDialog: typeConnection = $typeConnection"
//                                )*//*
//                                if (enableServeByIp) {
//                                    if (typeConnection == getString(
//                                            R.string.http_connection
//                                        ) || typeConnection == getString(
//                                            R.string.direct_connection
//                                        )
//                                    ) {
//// activar debug
////                                        e(
////                                            LogUtil.DIAM_PROXY_TAG,
////                                            TAG + "createAdvanceSettingsDialog: buscando server ip..."
////                                        )
//                                        server = ServerModelArrayList().getServerByDomainName(
//                                            domain,
//                                            ServerModelArrayList().serverList
//                                        )!!.ip
//                                    } else if (typeConnection == getString(
//                                            R.string.payload_connection
//                                        )
//                                    ) {
//                                        server = ServerModelArrayList().getServerByDomainName(
//                                            domain,
//                                            ServerModelArrayList().serverPayloadList
//                                        )!!.ip
//                                    } else if (typeConnection == getString(
//                                            R.string.fastdns_connection
//                                        )
//                                    ) {
//                                        server = ServerModelArrayList().getServerByDomainName(
//                                            domain,
//                                            ServerModelArrayList().serverFastDnsList
//                                        )!!.ip
//                                    } else {
//                                        server = ServerModelArrayList().getServerByDomainName(
//                                            domain,
//                                            ServerModelArrayList().serverDirectList
//                                        )!!.ip
//                                    }
//                                } else {
//                                    if (typeConnection == getString(
//                                            R.string.http_connection
//                                        ) || typeConnection == getString(
//                                            R.string.direct_connection
//                                        )
//                                    ) {
//                                        server = ServerModelArrayList().getServerByIp(
//                                            domain,
//                                            ServerModelArrayList().serverList
//                                        )!!.domainName
//                                    } else if (typeConnection == getString(
//                                            R.string.payload_connection
//                                        )
//                                    ) {
//                                        server = ServerModelArrayList().getServerByIp(
//                                            domain,
//                                            ServerModelArrayList().serverPayloadList
//                                        )!!.domainName
//                                    } else if (typeConnection == getString(
//                                            R.string.fastdns_connection
//                                        )
//                                    ) {
//                                        server = ServerModelArrayList().getServerByIp(
//                                            domain,
//                                            ServerModelArrayList().serverFastDnsList
//                                        )!!.domainName
//                                    } else {
//                                        server = ServerModelArrayList().getServerByIp(
//                                            domain,
//                                            ServerModelArrayList().serverDirectList
//                                        )!!.domainName
//                                    }
//                                }
//
//                                DmSharedPreferences
//                                    .getInstance(this@MainActivity)!!
//                                    .putString(
//                                        DmPrefsKeys.SHARED_PREFERENCES_SERVER,
//                                        server
//                                    )
//                                dialog.dismiss()
//                            }
//
//                        }
//                    )
//                } else {
//                    Utils.createAlertDialog(
//                        this@MainActivity,
//                        resources.getString(R.string.connection_active),
//                        resources.getString(R.string.connection_active_msg),
//                        false,
//                        null
//                    )
//                }
//                false
//            }
//            else -> false
//        }
//    }*/
//
//    private val onPageChangeListener: ViewPager.OnPageChangeListener = object :
//        ViewPager.OnPageChangeListener {
//        override fun onPageScrolled(
//            position: Int,
//            positionOffset: Float,
//            positionOffsetPixels: Int
//        ) {
//        } // nothing needed here
//
//        override fun onPageSelected(position: Int) {
//            // AppoDealAdManager.Instance().showinterstitial(this@MainActivity)
//        }
//
//        override fun onPageScrollStateChanged(state: Int) {} // nothing needed here
//    }
//
//    internal inner class SectionsPagerAdapter(fm: FragmentManager?) :
//        FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//        override fun getItem(position: Int): Fragment {
//            startFragment =
//                StartFragment.newInstance(
//                    object :
//                        StartFragment.OnFragmentInteractionListener {
//                        override fun onFragmentStartInteraction() {
////                            startVpn()
//                            LogFragment.addLog(this@MainActivity.getString(R.string.sendinjectormethod))
//                            Handler().postDelayed({ HTTPtunnelIntentService.start(applicationContext)
//                            }, 500)
//                        }
//
//                        override fun onFragmentStopInteraction() {
//                            LogFragment.addLog(this@MainActivity.getString(R.string.stopinjectormethod))
//                            stopTunnelService()
////                            stopVpn()
//                        }
//                    })
//            HTTPtunnelIntentService.checkStatus(this@MainActivity)
//
//            if (intentImport != null) {
//                startFragment!!.setFileImportedPath(intentImport!!.data!!.path)
//            }
//
//            return startFragment!!
//        }
//
//        override fun getCount(): Int {
//            val res: Resources = resources
//            val tabs: Array<String?> = res.getStringArray(R.array.tabs_array)
//            return tabs.size
//        }
//
//        override fun getPageTitle(position: Int): CharSequence? {
//            val res: Resources = resources
//            val tabs: Array<String> = res.getStringArray(R.array.tabs_array)
//            return tabs[position]
//        }
//    }
//
//    private fun stopTunnelService() {
//        LogFragment.addLog(this@MainActivity.getString(R.string.stopTunnelService))
//        //LogUtil.v(LogUtil.DIAM_PROXY_TAG, TAG  // activar debug + "stopTunnelService")
//        try {
//            val intent = Intent(this, HTTPtunnelIntentService::class.java)
//            stopService(intent)
//        } catch (e: Exception) {
//        }
//    }
//
//    private fun removeBroadcastWifi() {
//        try {
//            unregisterReceiver(brcWifiConnected)
//        } catch (e: Exception) {
//            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "unregisterReceiver(brcWifiConnected).error")
//        }
//        if (brcWifiConnected != null) brcWifiConnected = null
//    }
//
//    private fun removeBroadcastWifiAp() {
//        try {
//            unregisterReceiver(brcWifiApStatus)
//        } catch (e: Exception) {
//            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "unregisterReceiver(brcWifiConnected).error")
//        }
//        if (brcWifiApStatus != null) brcWifiApStatus = null
//    }
//
//    private fun removeBroadcastVpn() {
//        try {
//            unregisterReceiver(brcVpnConnected)
//        } catch (e: Exception) {
//            //e(LogUtil.DIAM_PROXY_TAG // activar debug, TAG + "unregisterReceiver(brcVpnConnected).error")
//        }
//        if (brcVpnConnected != null) brcVpnConnected = null
//    }
//
//    private fun removeBroadcastDeveloperMode() {
//        try {
//            unregisterReceiver(brcSettingDeveloperMode)
//        } catch (e: Exception) {
//
//        }
//        if (brcSettingDeveloperMode != null) brcSettingDeveloperMode = null
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//
//
//
//
//        HTTPtunnelIntentService.checkStatus(this)
//        if (DmSharedPreferences.getInstance(this)!!.getBoolean(
//                DmPrefsKeys.SHARED_PREFERENCES_NEW_VERSION,
//                DmPrefsKeys.SHARED_PREFERENCES_NEW_VERSION_DEFAULT
//            )
//        ) {
//            stopTunnelService()
//            stopService()
//            Utils.createAlertDialog(
//                this,
//                resources.getString(R.string.dialog_new_version_title),
//                resources.getString(R.string.dialog_new_version_content),
//                true,
//                object : Utils.DialogClickListener {
//                    override fun onDialogPositiveClickListener() {
//                        Utils.openAppOnPlayStore(
//                            this@MainActivity,
//                            "com.nicadevelop.nicavpn"
//                        )
//                        finish()
//                    }
//                }
//            )
//        }
//
////        Appodeal.show(this@MainActivity, Appodeal.BANNER_VIEW)
//    }
//
//    override fun onDestroy() {
//        removeBroadcastWifi()
//        removeBroadcastDeveloperMode()
//        removeBroadcastVpn()
//        removeBroadcastWifiAp()
//        checkHooking()
//        checkVPNStatus()
//        super.onDestroy()
//    }
//
//    override fun onServiceConnected(name: ComponentName?, service: IBinder) {
//        e(LogUtil.DIAM_PROXY_TAG + "V2RAY", "MainActivity.onServiceConnected")
//        needUnbind = true
//        val controller: ServerClientService.Controller = service as ServerClientService.Controller
//        serviceControl = controller.control
//        server = serviceControl!!.clientServer
//        if (server == null) {
////            stopVpn()
//            val serverPort = DmSharedPreferences
//                .getInstance(this@MainActivity)!!
//                .getInt(
//                    DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT,
//                    DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT_DEFAULT.toInt()
//                )
//            //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: serverPort->$serverPort")
//
//            val serverAddress = DmSharedPreferences
//                .getInstance(this@MainActivity)!!
//                .getString(
//                    DmPrefsKeys.SHARED_PREFERENCES_SERVER,
//                    DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
//                )
//            //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: serverAddress->$serverAddress")
//
//            val payloadConfig = DmSharedPreferences
//                .getInstance(this@MainActivity)!!
//                .getString(
//                    DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG,
//                    DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG_DEFAULT
//                )
//            // Frida detect
//            //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG: PayloadConfig->$payloadConfig")
//
//            serviceControl!!.setClientServer(serverAddress, serverPort, payloadConfig!!)
//            server = serviceControl!!.clientServer
//
//            Handler().postDelayed({
//                startService()
//            }, 500)
//            // startService()
//        }
////        startVpn()
//    }
//
//    override fun onServiceDisconnected(name: ComponentName?) {
//        e(LogUtil.DIAM_PROXY_TAG + "V2RAY", "MainActivity.onServiceDisconnected")
////        stopVpn()
//        if (server != null) server!!.close()
//        server = null
//        serviceControl = null
//        stopService(Intent(this, ServerClientService::class.java))
//    }
//
//    private fun unbindService() {
//        if (needUnbind) {
//            needUnbind = false
//            unbindService(this)
//        }
//    }
//
//    fun startBindService() {
//        bindService(
//            Intent(this@MainActivity, ServerClientService::class.java),
//            this@MainActivity,
//            Context.BIND_AUTO_CREATE
//        )
//    }
//
//    private fun startService() {
//        DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
//            DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
//            true
//        )
//
//        val i = Intent(this, ServerClientService::class.java)
//        startService(i)
//    }
//
//    fun stopService() {
//        DmSharedPreferences.getInstance(this@MainActivity)!!.putBoolean(
//            DmPrefsKeys.SHARED_PREFERENCES_STARTED_VPN,
//            false
//        )
//
//        e(LogUtil.DIAM_PROXY_TAG + "V2RAY", "MainActivity.stopService")
////        stopVpn()
//        unbindService()
//        stopPinger()
//        serviceControl = null
//        try {
//            val i = Intent(this, ServerClientService::class.java)
//            stopService(i)
//        } catch (e: Exception) {
//        }
//    }
//
//    /**
//     * Pinger
//     */
//    var pingHandler: Handler? = null
//    private val checkPingTask = object : Runnable {
//        override fun run() {
//            try {
//                Utils.getLatency(
//                    this@MainActivity,
//                    DmSharedPreferences
//                        .getInstance(this@MainActivity)!!
//                        .getString(
//                            DmPrefsKeys.SHARED_PREFERENCES_SERVER,
//                            DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
//                        ).toString()
//                )
//            } catch (e: java.lang.Exception) {
//                //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.ping error", e)
//            }
//            pingHandler!!.postDelayed(this, 40 * 1000)
//        }
//    }
//
//    @Throws(java.lang.Exception::class)
//    private fun startPinger() {
//        //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.starting pinger")
//        stopPinger()
//        pingHandler = Handler(Looper.getMainLooper())
//        pingHandler!!.postDelayed(checkPingTask, 0)
//    }
//
//    @Synchronized
//    private fun stopPinger() {
//        try {
//            if (pingHandler != null) {
//                pingHandler!!.removeCallbacks(checkPingTask)
//            }
//        } catch (e: java.lang.Exception) {
//            //e(LogUtil.DIAM_PROXY_TAG // activar debug, "$TAG.stopPinger: $e")
//        }
//    }
//
//    private fun isImportFile(context: Context): Boolean {
//        return DmSharedPreferences.getInstance(context)!!.getBoolean(
//            DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF,
//            DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF_DEFAULT
//        )
//    }
//
//    companion object {
//        const val ACTION_MAIN_WIFI_CONNECTED = "com.sock.diam.proxy.ACTION_MAIN_BCR_WIFI_CONNECTED"
//        const val ACTION_MAIN_DEVELOPER_SETTINGS =
//            "com.sock.diam.proxy.ACTION_MAIN_BCR_DEVELOPER_SETTINGS"
//        const val DEBUGGER_CONNECTED =
//            "com.sock.diam.proxy.DEBUGGER_CONNECTED"
//        const val CHANNEL_ID = "NOTIFY_CHANNEL_1"
//        const val FILE_REQUEST_CODE = 9000
//    }
//}