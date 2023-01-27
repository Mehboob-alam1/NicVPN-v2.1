package com.nicadevelop.nicavpn

import android.R.layout
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nicadevelop.nicavpn.Api_Fetch_Service.Api
import com.nicadevelop.nicavpn.Api_Fetch_Service.api_data_model_updated
import com.nicadevelop.nicavpn.Api_Fetch_Service.api_response
import com.nicadevelop.nicavpn.Application_Class.Application
import com.nicadevelop.nicavpn.Constant.Constant
import com.nicadevelop.nicavpn.Constant.Constant.isNetworkAvailable
import com.nicadevelop.nicavpn.Constant.Constant.isOven_Vpn_ConnectionActive
import com.nicadevelop.nicavpn.R.array
import com.nicadevelop.nicavpn.R.string
import com.nicadevelop.nicavpn.adapters.ServerPortAdapter
import com.nicadevelop.nicavpn.models.ServerModelPort
import com.nicadevelop.nicavpn.service.DnsService
import com.nicadevelop.nicavpn.service.HTTProcessManager
import com.nicadevelop.nicavpn.service.HTTPtunnelIntentService
import com.nicadevelop.nicavpn.tools.LogUtil
import com.nicadevelop.nicavpn.tools.LogUtil.e
import com.nicadevelop.nicavpn.tools.Utils
import com.nicadevelop.nicavpn.tools.Utils.generatePasswordKey
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsKeys
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsMethods
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmSharedPreferences
import com.sdsmdg.tastytoast.TastyToast
import com.v2ray.ang.util.AngConfigManager
import com.v2ray.ang.util.MmkvManager
import com.v2ray.ang.viewmodel.MainViewModel
import de.blinkt.openvpn.ClientManager
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.activities.DisconnectVPN
import de.blinkt.openvpn.core.OpenVPNService
import io.michaelrocks.paranoid.Obfuscate
import me.lfasmpao.dnstunnel.core.DnsServersDetector
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Obfuscate
class StartFragment : Fragment() {
    private var mListener: OnFragmentInteractionListener? = null
    private var textErrorSni: TextInputLayout? = null
    private var etSNI: EditText? = null
    private var textErrorDns: TextInputLayout? = null
    var choose_Server_preference: SharedPreferences? = null
    var api_response_modelArrayList: ArrayList<api_response>? = null
    var selectedport: ArrayList<Int>? = null
    var timer_connecting: CountDownTimer? = null

    var timer_layout: ConstraintLayout? = null
    var timeLeftProgress: ProgressBar? = null
    private var timeLeft_val: TextView? = null

    private var moreTime: TextView? = null
    private var layoutad: RelativeLayout? = null
    private var adView: AdView? = null
    var v2rayportudp: String? = "true"
    var api_model_updated: api_response? = null

    private var etDns: EditText? = null
    private var etDtt: EditText? = null

    private var seekBarPing: SeekBar? = null
    private var tvPing: TextView? = null
    private var startSwitch: SwitchCompat? = null
    private var start_ping: SwitchCompat? = null
    private var mAdView: AdView? = null
    private var mAdView1: AdView? = null

    private var open_server_list: RelativeLayout? = null
    private var flag_icon_list: ImageView? = null
    private var server_country_list: TextView? = null

    public var sharedPreferences: SharedPreferences? = null


    private var open_server_list_host: RelativeLayout? = null
    private var flag_icon_list_host: ImageView? = null
    private var server_country_list_host: TextView? = null

    private var v2ray_tcp: ConstraintLayout? = null
    private var v2ray_udp: ConstraintLayout? = null
    //   var item: String?

    private var item: String? = null
    private var spServerPort: Spinner? = null
    private var spPayloadServerPort: Spinner? = null
    private var spConnectionMethod: Spinner? = null


    private var txt_dnstt: TextView? = null
    private var txt_fastdns: TextView? = null
    private var txt_openvpn: TextView? = null
    private var txt_v2ray: TextView? = null


    private var rgDNSTT: RadioGroup? = null
    private var rbUDP: RadioButton? = null
    private var rbDOT: RadioButton? = null
    private var rbDOH: RadioButton? = null

    private var layoutHost: LinearLayout? = null
    private var layoutPayload: LinearLayout? = null
    private var layoutFastDns: LinearLayout? = null
    private var layoutDnsTT: LinearLayout? = null
    private var layoutV2RayNG: LinearLayout? = null

    private var viewContent: View? = null

    private var udpandtcp: LinearLayout? = null

    //    private var serverAdapter: ServerAdapter? = null
    private var serverList: ArrayList<api_response>? = null
    private var pingContainer: LinearLayout? = null
    private var connectionMethodContainer: LinearLayout? = null

    private var btnHttpRequestStart: AppCompatButton? = null
    private var btnFastDNSStart: ImageView? = null
    private var btnDNSTTStart: ImageView? = null
    private var btnStartV2RayCC: ImageView? = null

    private var textErrorPayload: LinearLayout? = null
    private var etPayload: EditText? = null
    private var txtEFCustomMsg: TextView? = null
    private var fileImportedPath: String? = null
    private var adapter: ArrayAdapter<CharSequence>? = null
    private var serverPortCont: LinearLayout? = null
    private var spPayloadServerPortCont: LinearLayout? = null

    private var serverFasDNSCont: LinearLayout? = null
    private var serverDNSTT: LinearLayout? = null

    private var btnUseSystemDns: ImageButton? = null

    // FRIDA DETECT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        System.loadLibrary(Constants.LibUltima)
        startSwitch!!.isEnabled = true
        startSwitch!!.setText(string.run_status_not_running)
        AppoDealAdManager.Instance().loadBanner(activity)
        val changeListener = OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {

                if (isHttpConnection(requireContext()) && getConfiguredSni(
                        requireContext()
                    )!!.isEmpty()
                ) {
                    if (context != null) {
                        textErrorSni!!.error =
                            requireContext().resources.getString(string.host_field)
                    }
                    startSwitch!!.isChecked = false
                } else if (isDnsConnection(requireContext()) && getConfiguredDNS(
                        requireContext()
                    )!!.isEmpty()
                ) {
                    if (context != null) {
                        textErrorDns!!.error =
                            requireContext().resources.getString(string.dns_field)
                    }
                    startSwitch!!.isChecked = false
                } else {
                    if (mListener != null) {
                        seekBarPing!!.isEnabled = false
                        start_ping!!.isEnabled = false
                        open_server_list!!.isEnabled = false
                        spServerPort!!.isEnabled = false
                        etSNI!!.isEnabled = false
                        spConnectionMethod!!.isEnabled = false
                        etDns!!.isEnabled = false


                        // attempt extraction in activity, to make service start faster

                        if (context != null) {
                            var customSni = getConfiguredSni(requireContext())
                            if (customSni != "") customSni = Constants.SNI_LABEL + customSni


                            if (isDnsConnection(requireContext()) || isDirectConnection(
                                    requireContext()
                                ) || isFDnsConnection(requireContext()) || isOpenVpnConnection(
                                    requireContext()
                                ) || isUdpModeConnection(requireContext())
                            ) customSni = ""

                            HTTProcessManager.setupConfig(
                                requireContext(), Constants.DEF_CONFIG
                                        // =================================================
                                        // Getting Custom Server
                                        // =================================================
                                        + Constants.SERVER_LABEL + getConfiguredServer(
                                    requireContext()
                                )
                                        // =================================================
                                        // Getting Custom SNI
                                        // =================================================
                                        + customSni
                            )
                        }
                        startSwitch!!.setText(string.run_status_starting)
                        mListener!!.onFragmentStartInteraction()
                    }
                }
            } else {
                etSNI!!.isEnabled = true
                start_ping!!.isEnabled = true
                seekBarPing!!.isEnabled = true
                if (mListener != null) {
                    startSwitch!!.setText(string.run_status_stopping)
                    mListener!!.onFragmentStopInteraction()
                }
            }
        }
        startSwitch!!.setOnCheckedChangeListener(changeListener)
        activity ?: return
        HTTPtunnelIntentService.isRunning.observe(viewLifecycleOwner) { aBoolean: Boolean ->
            if (aBoolean) {
                startSwitch!!.setText(string.run_status_running)
                startSwitch!!.setOnCheckedChangeListener(null)
                startSwitch!!.isChecked = true
                start_ping!!.isEnabled = false
                seekBarPing!!.isEnabled = false
                open_server_list!!.isEnabled = false
                spServerPort!!.isEnabled = false
                etSNI!!.isEnabled = false
                spConnectionMethod!!.isEnabled = false



                etDns!!.isEnabled = false
                startSwitch!!.setOnCheckedChangeListener(changeListener)
            } else {
                startSwitch!!.setText(string.run_status_not_running)
                startSwitch!!.setOnCheckedChangeListener(null)
                startSwitch!!.isChecked = false
                start_ping!!.isEnabled = true
                seekBarPing!!.isEnabled = start_ping!!.isChecked
                open_server_list!!.isEnabled = true
                spServerPort!!.isEnabled = true
                etSNI!!.isEnabled = true
                spConnectionMethod!!.isEnabled = true
                etDns!!.isEnabled = true



                startSwitch!!.setOnCheckedChangeListener(changeListener)
            }
        }

/*        if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_FIRST_INSTALLATION,
                DmPrefsKeys.SHARED_PREFERENCES_FIRST_INSTALLATION_DEFAULT
            ) && !com.nicadevelop.nicavpn.BuildConfig.DEBUG
        ) {
            Utils.batteryWarningDialog(requireContext())


            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_FIRST_INSTALLATION,
                false
            )
        }*/
    }


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        generatePasswordKey(requireContext())


        // Inflate the layout for this fragment
        viewContent = inflater.inflate(R.layout.fragment_start, container, false)
        val mainViewModel: MainViewModel by viewModels()

        choose_Server_preference =
            requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)

        api_response_modelArrayList = ArrayList()
        serverList = ArrayList()

        startSwitch = viewContent!!.findViewById(R.id.start_switch)
        start_ping = viewContent!!.findViewById(R.id.start_ping)
        seekBarPing = viewContent!!.findViewById(R.id.seekBarPing)
        timeLeft_val = viewContent!!.findViewById(R.id.timeLeft_val)
        moreTime = viewContent!!.findViewById(R.id.moreTime)
        textErrorSni = viewContent!!.findViewById(R.id.input_layout_sni)
        etSNI = viewContent!!.findViewById(R.id.etSNI)
        timer_layout = viewContent!!.findViewById(R.id.time_layout)

        textErrorDns = viewContent!!.findViewById(R.id.input_layout_dns)
        etDns = viewContent!!.findViewById(R.id.etDNS)
        etDtt = viewContent!!.findViewById(R.id.etDtt)

        tvPing = viewContent!!.findViewById(R.id.tvPing)

        spServerPort = viewContent!!.findViewById(R.id.spServerPort)
        spPayloadServerPort = viewContent!!.findViewById(R.id.spPayloadServerPort)

        spServerPort!!.avoidDropdownFocus()
        spPayloadServerPort!!.avoidDropdownFocus()

        spConnectionMethod = viewContent!!.findViewById(R.id.spConnectionMethod)

        //set initial value..!!
        set_initial_value_time()

        txt_dnstt = viewContent!!.findViewById(R.id.txt_dnstt)
        txt_fastdns = viewContent!!.findViewById(R.id.txt_fastdns)
        txt_openvpn = viewContent!!.findViewById(R.id.txt_openvpn)
        txt_v2ray = viewContent!!.findViewById(R.id.txt_v2ray)

        udpandtcp = viewContent!!.findViewById(R.id.udpandtcp)
        adView = AdView(requireContext())
        layoutad = viewContent!!.findViewById(R.id.banner)
        v2ray_tcp = viewContent!!.findViewById(R.id.v2ray_tcp)
        v2ray_udp = viewContent!!.findViewById(R.id.v2ray_udp)

        layoutHost = viewContent!!.findViewById(R.id.layoutHost)
        layoutPayload = viewContent!!.findViewById(R.id.layoutPayload)
        layoutFastDns = viewContent!!.findViewById(R.id.layoutFastDns)
        layoutDnsTT = viewContent!!.findViewById(R.id.layoutDnsTT)
        layoutV2RayNG = viewContent!!.findViewById(R.id.layoutV2RayNG)

        rgDNSTT = viewContent!!.findViewById(R.id.rgDNSTT)
        rbUDP = viewContent!!.findViewById(R.id.rbUDP)
        rbDOT = viewContent!!.findViewById(R.id.rbDOT)
        rbDOH = viewContent!!.findViewById(R.id.rbDOH)

        pingContainer = viewContent!!.findViewById(R.id.pingContainer)
        connectionMethodContainer = viewContent!!.findViewById(R.id.connectionMethodContainer)

//        start button
        open_server_list = viewContent!!.findViewById(R.id.open_server_list)
        open_server_list!!.isEnabled = true


        open_server_list_host = viewContent!!.findViewById(R.id.open_server_list_host)

        flag_icon_list = viewContent!!.findViewById(R.id.flag_icon_list)
        server_country_list = viewContent!!.findViewById(R.id.server_country_list)



        server_country_list!!.text =
            getBestServer_after_calculation(choose_Server_preference)!!.hostName


        btnHttpRequestStart = viewContent!!.findViewById(R.id.btnHttpRequestStart)
        btnFastDNSStart = viewContent!!.findViewById(R.id.btnFastDNSStart)
        btnDNSTTStart = viewContent!!.findViewById(R.id.btnDNSTTStart)
        btnStartV2RayCC = viewContent!!.findViewById(R.id.btnStartCustomV2Ray)

        textErrorPayload = viewContent!!.findViewById(R.id.input_layout_payload)
        etPayload = viewContent!!.findViewById(R.id.etPayload)
        txtEFCustomMsg = viewContent!!.findViewById(R.id.txtEFCustomMsg)
        serverPortCont = viewContent!!.findViewById(R.id.serverPortCont)
        spPayloadServerPortCont = viewContent!!.findViewById(R.id.spPayloadServerPortCont)
        serverDNSTT = viewContent!!.findViewById(R.id.serverDNSTT)
        serverFasDNSCont = viewContent!!.findViewById(R.id.serverFasDNSCont)

        btnUseSystemDns = viewContent!!.findViewById(R.id.btnUseSystemDns)

        v2rayportudp = "true"

        loadAdmobBanner()
        val tvSKey = viewContent!!.findViewById<TextView>(R.id.tvSKey)


        if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS_DEFAULT
            )
        ) {


            if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                timer_layout!!.visibility = GONE
            } else {
                timer_layout!!.visibility = VISIBLE
            }
            btnFastDNSStart!!.setImageResource(R.drawable.connected)
            //  btnFastDNSStart!!.text = requireContext().resources.getString(string.payload_stop)
        } else {
            btnFastDNSStart!!.setImageResource(R.drawable.disconnected)
            timer_layout!!.visibility = GONE
            //     btnFastDNSStart!!.text = requireContext().resources.getString(string.payload_start)
        }

        if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT_DEFAULT
            )
        ) {
            btnDNSTTStart!!.setBackgroundResource(R.drawable.connected);
            if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                timer_layout!!.visibility = GONE
            } else {
                timer_layout!!.visibility = VISIBLE
            }
        } else {
            btnDNSTTStart!!.setBackgroundResource(R.drawable.disconnected);
            timer_layout!!.visibility = GONE
        }
        if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY_DEFAULT
            )
        ) {
            btnStartV2RayCC!!.setBackgroundResource(R.drawable.connected);
            if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                timer_layout!!.visibility = GONE
            } else {
                timer_layout!!.visibility = VISIBLE
            }
        } else {
            btnStartV2RayCC!!.setBackgroundResource(R.drawable.disconnected);
            timer_layout!!.visibility = GONE
        }

        if (context != null) {
            etSNI!!.setText(getConfiguredSni(requireContext()))
            etDns!!.setText(getConfiguredDNS(requireContext()))
            etDtt!!.setText(getConfiguredDNSTT(requireContext()))
            etPayload!!.setText(getConfiguredPayload(requireContext()))

            val dnsTTConnection = DmPrefsMethods.getConnectionDnsTT()
            if (dnsTTConnection === "UDP") {
                rbUDP!!.isChecked = true
            } else if (dnsTTConnection === "DOT") {
                rbDOT!!.isChecked = true
            } else if (dnsTTConnection === "DOH") {
                rbDOH!!.isChecked = true
            }

            txtEFCustomMsg!!.text = DmSharedPreferences.getInstance(requireContext())!!.getString(
                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG,
                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG_DEFAULT
            )
        }

        rbUDP!!.setOnClickListener { v ->
            if (v != null) {
                onRbClickedDNSTT(v)
            }
        }
        rbDOT!!.setOnClickListener { v ->
            if (v != null) {
                onRbClickedDNSTT(v)
            }
        }
        rbDOH!!.setOnClickListener { v ->
            if (v != null) {
                onRbClickedDNSTT(v)
            }
        }


        val dnsTtType = DmPrefsMethods.getConnectionDnsTT()
        if (dnsTtType == DmPrefsMethods.DnsTtType.UDP.value) {
            rbUDP!!.isChecked = true
        }
        if (dnsTtType == DmPrefsMethods.DnsTtType.DOT.value) {
            rbDOT!!.isChecked = true
        }
        if (dnsTtType == DmPrefsMethods.DnsTtType.DOH.value) {
            rbDOH!!.isChecked = true
        }

        etSNI!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                @Suppress("NAME_SHADOWING") var charSequence = charSequence
                if (context != null && !isImportFile(requireContext())) {
                    when {
                        charSequence.toString() == Constants.DEAFULT_HOST_NICKNAME -> {
                            charSequence = Constants.DEAFULT_HOST
                        }
                    }
                    DmSharedPreferences.getInstance(requireContext())!!
                        .putString(DmPrefsKeys.SHARED_PREFERENCES_SNI, charSequence.toString())
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        etDns!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isImportFile(requireContext())) {
                    DmSharedPreferences.getInstance(requireContext())!!
                        .putString(DmPrefsKeys.SHARED_PREFERENCES_DNS, s.toString())
                }
            }

        })
        etDtt!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isImportFile(requireContext())) {
                    DmPrefsMethods.setDnsTTHost(s.toString())
                }
            }

        })

        etPayload!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isImportFile(requireContext())) {
                    DmSharedPreferences.getInstance(requireContext())!!
                        .putString(DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG, s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        if (context != null) {
            val ping = DmSharedPreferences.getInstance(requireContext())!!.getLong(
                DmPrefsKeys.SHARED_PREFERENCES_PING, DmPrefsKeys.SHARED_PREFERENCES_PING_DEFAULT
            )
            val pingTmp = ping.toInt() / 1000
            seekBarPing!!.progress =
                (pingTmp - (DmPrefsKeys.SHARED_PREFERENCES_PING_DEFAULT + 1)).toInt()
            tvPing!!.text = pingTmp.toString()
        }
        seekBarPing!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                var ping = i + (DmPrefsKeys.SHARED_PREFERENCES_PING_DEFAULT / 1000)
                tvPing!!.text = ping.toString()
                ping = (i * 1000) + DmPrefsKeys.SHARED_PREFERENCES_PING_DEFAULT
                if (context != null) {
                    DmSharedPreferences.getInstance(requireContext())!!
                        .putLong(DmPrefsKeys.SHARED_PREFERENCES_PING, ping.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        if (startSwitch!!.isChecked) {
            seekBarPing!!.isEnabled = false
            etSNI!!.isEnabled = false
        } else {
            seekBarPing!!.isEnabled = true
            etSNI!!.isEnabled = true
        }
        start_ping!!.isChecked = DmPrefsMethods.getCustomPing()
        seekBarPing!!.isEnabled = start_ping!!.isChecked

        start_ping!!.setOnCheckedChangeListener { _: CompoundButton?, b: Boolean ->
            if (context != null) {
                DmPrefsMethods.setCustomPing(b)
            }
            seekBarPing!!.isEnabled = b
        }

        if (context != null) {


            spServerPort!!.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    onServerPortSelectedListener(parent, position)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

            spPayloadServerPort!!.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    onServerPortSelectedListener(parent, position)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }

        }

        if (context != null) {
            val connectionMethods =
                requireContext().resources.getStringArray(array.connection_methods)
            for (connectionMethod in connectionMethods) {

            }
            adapter = ArrayAdapter.createFromResource(
                requireContext(), array.connection_methods, layout.simple_spinner_item
            )
            adapter!!.setDropDownViewResource(layout.simple_spinner_dropdown_item)
            spConnectionMethod!!.adapter = adapter

            var typeConnection = getTypeConnection(requireContext())



            txt_fastdns!!.setOnClickListener {


                if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else {

                    udpandtcp!!.visibility = GONE
                    spConnectionMethod!!.setSelection(adapter!!.getPosition(getString(string.fastdns_connection)))

                    item = requireContext().getString(string.fastdns_connection)

                    typeConnection = if (item.toString()
                            .trim() == requireContext().getString(string.fastdns_connection)
                    ) {
                        requireContext().getString(string.fastdns_connection)
                    } else if (item.toString()
                            .trim() == requireContext().getString(string.UDP_connection)
                    ) {
                        requireContext().getString(string.UDP_connection)
                    } else {
                        requireContext().getString(string.open_vpn_connection)
                    }

                    if (typeConnection!!.contains(requireContext().getString(string.fastdns_connection))) {

                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()


                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                            //                                            ServerModelArrayList().serverFastDnsList
                        }
                    } else if (typeConnection!!.contains(requireContext().getString(string.UDP_connection))) {
                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()
                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {


                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                            //                                            ServerModelArrayList().serverFastDnsList
                        }
                    } else {
                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()
                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                            //                                            ServerModelArrayList().serverFastDnsList
                        }
                    }



                    etSNI!!.isEnabled = false
                    etDns!!.isEnabled = false

                    layoutHost!!.visibility = View.GONE
                    layoutPayload!!.visibility = View.GONE
                    layoutFastDns!!.visibility = View.VISIBLE
                    layoutV2RayNG!!.visibility = View.GONE
                    serverFasDNSCont!!.visibility = View.GONE
                    layoutDnsTT!!.visibility = View.GONE

                    textErrorSni!!.visibility = View.GONE
                    textErrorDns!!.visibility = View.GONE

                    txt_fastdns!!.setTextColor(resources.getColor(R.color.colorSelected))
                    txt_dnstt!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_openvpn!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_v2ray!!.setTextColor(resources.getColor(R.color.colornonSelected))


                    server_country_list!!.text =
                        getBestServer_after_calculation(choose_Server_preference)!!.hostName


                }


            }

            txt_dnstt!!.setOnClickListener {

                if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else {
                    spConnectionMethod!!.setSelection(adapter!!.getPosition(getString(string.DNSTT_connection)))

                    udpandtcp!!.visibility = GONE

                    item = requireContext().getString(string.DNSTT_connection)

                    typeConnection = requireContext().getString(string.DNSTT_connection)

                    txt_fastdns!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_dnstt!!.setTextColor(resources.getColor(R.color.colorSelected))
                    txt_openvpn!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_v2ray!!.setTextColor(resources.getColor(R.color.colornonSelected))

                    etSNI!!.isEnabled = false
                    etDns!!.isEnabled = false

                    layoutHost!!.visibility = View.GONE
                    layoutPayload!!.visibility = View.GONE
                    layoutFastDns!!.visibility = View.GONE
                    layoutV2RayNG!!.visibility = View.GONE
                    layoutDnsTT!!.visibility = View.VISIBLE
                    serverFasDNSCont!!.visibility = View.GONE
                    textErrorSni!!.visibility = View.GONE
                    textErrorDns!!.visibility = View.GONE





                    server_country_list!!.text =
                        getBestServer_after_calculation(choose_Server_preference)!!.hostName

                    if (choose_Server_preference != null) {
                        api_response_modelArrayList!!.clear()
                        serverList!!.clear()

                        api_response_modelArrayList = getAllServers_from_cache()


                        if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                            for (k in 0 until api_response_modelArrayList!!.size) {
                                serverList!!.add(api_response_modelArrayList!![k])
                            }
                        }
                        //                                            ServerModelArrayList().serverFastDnsList
                    }

                    if (serverList !== null) {
                        layoutDnsTT!!.visibility = View.VISIBLE
                        etDtt!!.isEnabled = true


                    }


                }

            }


            txt_openvpn!!.setOnClickListener {
                if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else {
                    spConnectionMethod!!.setSelection(adapter!!.getPosition(getString(string.open_vpn_connection)))

                    udpandtcp!!.visibility = VISIBLE

                    item = requireContext().getString(string.open_vpn_connection)

                    typeConnection = if (item.toString()
                            .trim() == requireContext().getString(string.fastdns_connection)
                    ) {
                        requireContext().getString(string.fastdns_connection)
                    } else if (item.toString()
                            .trim() == requireContext().getString(string.UDP_connection)
                    ) {
                        requireContext().getString(string.UDP_connection)
                    } else {
                        requireContext().getString(string.open_vpn_connection)
                    }

                    txt_fastdns!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_dnstt!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_openvpn!!.setTextColor(resources.getColor(R.color.colorSelected))
                    txt_v2ray!!.setTextColor(resources.getColor(R.color.colornonSelected))


                    etSNI!!.isEnabled = false
                    etDns!!.isEnabled = false

                    layoutHost!!.visibility = View.GONE
                    layoutPayload!!.visibility = View.GONE
                    layoutFastDns!!.visibility = View.VISIBLE
                    layoutV2RayNG!!.visibility = View.GONE
                    layoutDnsTT!!.visibility = View.GONE
                    serverFasDNSCont!!.visibility = View.GONE
                    textErrorSni!!.visibility = View.GONE
                    textErrorDns!!.visibility = View.GONE




                    server_country_list!!.text =
                        getBestServer_after_calculation(choose_Server_preference)!!.hostName





                    if (typeConnection!!.contains(requireContext().getString(string.fastdns_connection))) {

                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()


                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                            //                                            ServerModelArrayList().serverFastDnsList
                        }
                    } else if (typeConnection!!.contains(requireContext().getString(string.UDP_connection))) {
                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()
                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {


                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                            //                                            ServerModelArrayList().serverFastDnsList
                        }
                    } else {
                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()
                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                            //                                            ServerModelArrayList().serverFastDnsList
                        }
                    }


                }
            }


            txt_v2ray!!.setOnClickListener {
                if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN_DEFAULT
                    )
                ) {
                    TastyToast.makeText(
                        requireContext(),
                        "Disconnect VPN First",
                        TastyToast.LENGTH_LONG,
                        TastyToast.WARNING
                    ).show()
                } else {
                    item = requireContext().getString(string.custom_v2ray_icc_connection)
                    typeConnection = requireContext().getString(string.custom_v2ray_icc_connection)
                    udpandtcp!!.visibility = VISIBLE

                    spConnectionMethod!!.setSelection(adapter!!.getPosition(getString(string.custom_v2ray_icc_connection)))


                    txt_fastdns!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_dnstt!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_openvpn!!.setTextColor(resources.getColor(R.color.colornonSelected))
                    txt_v2ray!!.setTextColor(resources.getColor(R.color.colorSelected))

                    etSNI!!.isEnabled = false
                    etDns!!.isEnabled = false

                    layoutHost!!.visibility = View.GONE
                    layoutPayload!!.visibility = View.GONE
                    layoutFastDns!!.visibility = View.GONE
                    layoutV2RayNG!!.visibility = View.VISIBLE
                    layoutDnsTT!!.visibility = View.GONE
                    serverFasDNSCont!!.visibility = View.VISIBLE
                    textErrorSni!!.visibility = View.GONE
                    textErrorDns!!.visibility = View.GONE




                    server_country_list!!.text =
                        getBestServer_after_calculation(choose_Server_preference)!!.hostName


                    layoutV2RayNG!!.visibility = View.VISIBLE
                }
            }



            spConnectionMethod!!.onItemSelectedListener = object : OnItemSelectedListener {
                @SuppressLint("ClickableViewAccessibility")


                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    item = parent.selectedItem.toString()



                    if (spConnectionMethod!!.selectedItem.toString().isNotEmpty()) {


                    }


                    if (!isImportFile(requireContext())) {
                        setCustomDnsConnetion(
                            requireContext(), DmPrefsKeys.SHARED_PREFERENCES_DNS_CONNECTION_DEFAULT
                        )
                    } else {
                        item = getTypeConnection(requireContext())
                    }

                    @Suppress("NAME_SHADOWING") var typeConnection = ""
                    var setResetSniDefault = true
                    var setResetDnsDefault = true
                    var setResetPayloadDefaul = true

                    textErrorSni!!.isErrorEnabled = false
                    textErrorDns!!.isErrorEnabled = false
                    // textErrorPayload!!.isErrorEnabled = false

                    etSNI!!.isEnabled = false
                    etDns!!.isEnabled = false
                    etDtt!!.isEnabled = false

                    layoutHost!!.visibility = View.GONE
                    layoutPayload!!.visibility = View.GONE
                    layoutFastDns!!.visibility = View.GONE
                    layoutDnsTT!!.visibility = View.GONE
                    layoutV2RayNG!!.visibility = View.GONE

                    textErrorSni!!.visibility = View.GONE
                    textErrorDns!!.visibility = View.GONE

                    /** Direct Connection */
                    if (item!!.trim { it <= ' ' } == requireContext().getString(
                            string.direct_connection
                        ).trim { it <= ' ' }) {
                        typeConnection = requireContext().getString(string.direct_connection)
//                        serverList = ServerModelArrayList().serverList

                        api_model_updated = api_response()
                        api_model_updated!!.server_id = 10000
                        api_model_updated!!.hostName =
                            MyApplication.appContext.getString(string.automatic_server_name)
                        api_model_updated!!.ip = ""
                        api_model_updated!!.port = 0
                        api_model_updated!!.drawable = ""
                        api_model_updated!!.city = MyApplication.appContext.getString(
                            string.automatic_server_desc
                        )
                        api_model_updated!!.publickey = ""
                        serverList!!.add(api_model_updated!!)

                        etSNI!!.isEnabled = false
                        etDns!!.isEnabled = false

                        layoutHost!!.visibility = View.VISIBLE
                        layoutPayload!!.visibility = View.GONE
                        layoutFastDns!!.visibility = View.GONE

                        textErrorSni!!.visibility = View.GONE
                        textErrorDns!!.visibility = View.GONE
                    }
                    /** TLS Over HTTP Connection */
                    else if (item!!.trim { it <= ' ' } == requireContext().getString(
                            string.http_connection
                        ).trim { it <= ' ' }) {
                        typeConnection = requireContext().getString(string.http_connection)
                        setResetSniDefault = false

//                        serverList = ServerModelArrayList().serverList

                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()


                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                        }

                        etSNI!!.isEnabled = true
                        etDns!!.isEnabled = false

                        layoutHost!!.visibility = View.VISIBLE
                        layoutPayload!!.visibility = View.GONE
                        layoutFastDns!!.visibility = View.GONE

                        if (!DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                                DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF,
                                DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF_DEFAULT
                            )
                        ) {
                            textErrorSni!!.visibility = View.VISIBLE
                            textErrorDns!!.visibility = View.GONE
                        }
                    }
                    /** Payload Connection */
                    else if (item!!.trim { it <= ' ' } == requireContext().getString(
                            string.payload_connection
                        ).trim { it <= ' ' }) {
                        setResetPayloadDefaul = false
                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()


                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                        }

                        typeConnection = requireContext().getString(string.payload_connection)

                        layoutHost!!.visibility = View.GONE
                        layoutPayload!!.visibility = View.VISIBLE
                        layoutFastDns!!.visibility = View.GONE


                    }
                    /** HTTP Connection */
                    else if (item!!.trim { it <= ' ' } == requireContext().getString(
                            string.dns_connection
                        ).trim { it <= ' ' }) {
                        typeConnection = requireContext().getString(string.dns_connection)
                        setResetDnsDefault = false
//                        serverList = ServerModelArrayList().serverDirectList

                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()


                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                        }

                        etSNI!!.isEnabled = false
                        etDns!!.isEnabled = true

                        layoutHost!!.visibility = View.VISIBLE
                        layoutPayload!!.visibility = View.GONE
                        layoutFastDns!!.visibility = View.GONE

                        if (!isImportFile(requireContext())) {
                            textErrorSni!!.visibility = View.GONE
                            textErrorDns!!.visibility = View.VISIBLE
                        }

                        setCustomDnsConnetion(requireContext(), true)
                    }
                    /** Fast DNS, OpenVpn & UdpMode Connection */
                    else if (item!!.trim { it <= ' ' } == requireContext().getString(
                            string.fastdns_connection
                        ) || item!!.trim { it <= ' ' } == requireContext().getString(
                            string.open_vpn_connection
                        ) || item!!.trim { it <= ' ' } == requireContext().getString(
                            string.UDP_connection
                        ).trim { it <= ' ' }) {

                        server_country_list!!.text =
                            getBestServer_after_calculation(choose_Server_preference)!!.hostName

                        typeConnection = if (item.toString().trim() == requireContext().getString(
                                string.fastdns_connection
                            )
                        ) {
                            requireContext().getString(string.fastdns_connection)
                        } else if (item.toString()
                                .trim() == requireContext().getString(string.UDP_connection)
                        ) {
                            requireContext().getString(string.UDP_connection)
                        } else {
                            requireContext().getString(string.open_vpn_connection)
                        }



                        if (typeConnection.contains(requireContext().getString(string.fastdns_connection))) {

                            if (choose_Server_preference != null) {
                                api_response_modelArrayList!!.clear()
                                serverList!!.clear()

                                api_response_modelArrayList = getAllServers_from_cache()


                                if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                    for (k in 0 until api_response_modelArrayList!!.size) {
                                        serverList!!.add(api_response_modelArrayList!![k])
                                    }
                                }
                                //                                            ServerModelArrayList().serverFastDnsList
                            }
                        } else if (typeConnection.contains(requireContext().getString(string.UDP_connection))) {
                            if (choose_Server_preference != null) {
                                api_response_modelArrayList!!.clear()
                                serverList!!.clear()

                                api_response_modelArrayList = getAllServers_from_cache()
                                if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {


                                    for (k in 0 until api_response_modelArrayList!!.size) {
                                        serverList!!.add(api_response_modelArrayList!![k])
                                    }
                                }
                                //                                            ServerModelArrayList().serverFastDnsList
                            }
                        } else {
                            if (choose_Server_preference != null) {
                                api_response_modelArrayList!!.clear()
                                serverList!!.clear()

                                api_response_modelArrayList = getAllServers_from_cache()
                                if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                    for (k in 0 until api_response_modelArrayList!!.size) {
                                        serverList!!.add(api_response_modelArrayList!![k])
                                    }
                                }
                                //                                            ServerModelArrayList().serverFastDnsList
                            }
                        }

                        etSNI!!.isEnabled = false
                        etDns!!.isEnabled = false

                        layoutHost!!.visibility = View.GONE
                        layoutPayload!!.visibility = View.GONE
                        layoutFastDns!!.visibility = View.VISIBLE

                        textErrorSni!!.visibility = View.GONE
                        textErrorDns!!.visibility = View.GONE


                    }
                    /** DNS TT Connection */
                    else if (item!!.trim { it <= ' ' } == requireContext().getString(
                            string.DNSTT_connection
                        ).trim { it <= ' ' }) {
                        typeConnection = requireContext().getString(string.DNSTT_connection)
//                        serverList = ServerModelArrayList().serverDnsTTList

                        server_country_list!!.text =
                            getBestServer_after_calculation(choose_Server_preference)!!.hostName

                        if (choose_Server_preference != null) {
                            api_response_modelArrayList!!.clear()
                            serverList!!.clear()

                            api_response_modelArrayList = getAllServers_from_cache()


                            if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {

                                for (k in 0 until api_response_modelArrayList!!.size) {
                                    serverList!!.add(api_response_modelArrayList!![k])
                                }
                            }
                            //                                            ServerModelArrayList().serverFastDnsList
                        }

                        if (serverList !== null) {
                            layoutDnsTT!!.visibility = View.VISIBLE
                            etDtt!!.isEnabled = true


                        }
                    }
                    /** Sock All Custom Connection */


                    else if (item!!.trim { it <= ' ' } == requireContext().getString(
                            string.custom_v2ray_icc_connection
                        ).trim { it <= ' ' }) {
                        server_country_list!!.text =
                            getBestServer_after_calculation(choose_Server_preference)!!.hostName

                        typeConnection =
                            requireContext().getString(string.custom_v2ray_icc_connection)
                        layoutV2RayNG!!.visibility = View.VISIBLE


                    }




                    if (!isImportFile(requireContext())) {
                        if (setResetSniDefault) {
                            DmSharedPreferences.getInstance(requireContext())!!.putString(
                                DmPrefsKeys.SHARED_PREFERENCES_SNI,
                                DmPrefsKeys.SHARED_PREFERENCES_SNI_DEFAULT
                            )
                        }
                        if (setResetDnsDefault) {
                            DmSharedPreferences.getInstance(requireContext())!!.putString(
                                DmPrefsKeys.SHARED_PREFERENCES_DNS,
                                DmPrefsKeys.SHARED_PREFERENCES_DNS_DEFAULT
                            )
                        }
                        if (setResetPayloadDefaul) {
                            DmSharedPreferences.getInstance(requireContext())!!.putString(
                                DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG,
                                DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG_DEFAULT
                            )
                        }
                        DmSharedPreferences.getInstance(requireContext())!!.putString(
                            DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION, typeConnection
                        )
                    }

                    writeConn()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spConnectionMethod!!.setSelection(connectionMethods.indexOf(typeConnection))
        }

        if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_PAYLOAD,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_PAYLOAD_DEFAULT
            )
        ) {
            etPayload!!.isEnabled = false
            btnHttpRequestStart!!.text = requireContext().resources.getString(string.payload_stop)

            spConnectionMethod!!.isEnabled = false
            open_server_list!!.isEnabled = false
            spPayloadServerPort!!.isEnabled = false


        } else {
            etPayload!!.isEnabled = true
            btnHttpRequestStart!!.text = requireContext().resources.getString(string.payload_start)



            spConnectionMethod!!.isEnabled = true
            open_server_list!!.isEnabled = true
            spPayloadServerPort!!.isEnabled = true
        }


        System.loadLibrary(Constants.LibUltima)


        open_server_list!!.setOnClickListener {


            if (choose_Server_preference != null) {
                api_response_modelArrayList!!.clear()
                serverList!!.clear()

                api_response_modelArrayList = getAllServers_from_cache()

                if (api_response_modelArrayList != null && api_response_modelArrayList!!.isNotEmpty()) {
                    for (k in 0 until api_response_modelArrayList!!.size) {
                        serverList!!.add(api_response_modelArrayList!![k])
                    }
                    //                    serverAdapter = ServerAdapter(requireContext(), serverList!!)
                    //                    spServerv2ray!!.adapter = serverAdapter

                    if (serverList!!.isNotEmpty()) {
                        val intent = Intent(requireActivity(), Server_Class::class.java)
                        startActivity(intent)
                    }
                }
            }
        }





        btnHttpRequestStart!!.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                if (isNetworkAvailable(requireActivity())) {
                    if (etPayload!!.text.toString().isEmpty() && !isImportFile(requireContext())) {
                        //      textErrorPayload!!.error =
                        //        requireContext().resources.getString(string.payload_config_msg)
                        //    return
                    }
                    if (!isImportFile(requireContext())) {
                        DmSharedPreferences.getInstance(requireContext())!!.putString(
                            DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG,
                            etPayload!!.text.toString()
                        )
                    }
                    val mainActivity: MainActivity = activity as MainActivity
                    if (mainActivity.server == null) {
                        DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                            DmPrefsKeys.SHARED_PREFERENCES_STARTED_PAYLOAD, true
                        )
                        etPayload!!.isEnabled = false
                        mainActivity.startBindService()
                        btnHttpRequestStart!!.text =
                            requireContext().resources.getString(string.payload_stop)

                        spConnectionMethod!!.isEnabled = false
                        open_server_list!!.isEnabled = false
                        spPayloadServerPort!!.isEnabled = false

                    } else {
                        DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                            DmPrefsKeys.SHARED_PREFERENCES_STARTED_PAYLOAD, false
                        )

                        etPayload!!.isEnabled = true
                        mainActivity.server!!.close()
                        mainActivity.server = null
                        mainActivity.stopService()
                        btnHttpRequestStart!!.text =
                            requireContext().resources.getString(string.payload_start)

                        spConnectionMethod!!.isEnabled = true
                        open_server_list!!.isEnabled = true
                        spPayloadServerPort!!.isEnabled = true
                        moreTime!!.isEnabled = true
                    }
                } else {
                    Toast.makeText(requireActivity(), "Internet is required", Toast.LENGTH_LONG)
                        .show()
                }
            }
        })

        btnFastDNSStart!!.setOnClickListener {
            if (isNetworkAvailable(requireActivity())) {
                if (isOpenVpnConnection(requireContext())) {
                    openVpnConnection()
                } else if (isUdpModeConnection(requireContext())) {
                    udpModeConnection()
                } else {
                    fastDnsConnection()
                }
                moreTime!!.isEnabled = true
                moreTime!!.isClickable = true
            } else {
                Toast.makeText(requireActivity(), "Internet is required", Toast.LENGTH_LONG).show()
            }
        }



        btnUseSystemDns!!.setOnClickListener {
            val dnsServersDetector = DnsServersDetector(requireContext())
            etDtt!!.setText("${dnsServersDetector.servers[0]}:53")
        }

        btnDNSTTStart!!.setOnClickListener {
            if (isNetworkAvailable(requireActivity())) {
                if (!DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT_DEFAULT
                    )
                ) {
                    val mainActivity: MainActivity = activity as MainActivity
                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT, true
                    )

                    val i = Intent(context, DnsService::class.java)
                    i.putExtra(
                        "host", getBestServer_after_calculation(choose_Server_preference)!!.ip_dnstt
                    )
                    i.putExtra("extra", "53")
                    i.putExtra("dnstt_conn", DmPrefsMethods.getConnectionDnsTT())
                    i.putExtra("type_conn", getTypeConnection(requireContext()))
                    i.putExtra("dnstt_host", getConfiguredDNSTT(requireContext()))
                    i.putExtra(
                        "public_key",
                        getBestServer_after_calculation(choose_Server_preference)!!.publickey
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireContext().startForegroundService(i)
                    } else {
                        requireContext().startService(i)
                    }
                    importClipboardHard()


                    mainActivity.startVpn()


                    if (timer_connecting != null) timer_connecting!!.cancel()

                    timer_connecting = object : CountDownTimer(10000, 100) {
                        override fun onTick(millisUntilFinished: Long) {
                            if (isOven_Vpn_ConnectionActive()) {

                                timer_connecting?.cancel()

                                if (activity != null && isAdded) {
                                    start_service_to_calculate()
                                }
                            }
                        }

                        override fun onFinish() {
                            (activity as MainActivity).stopService(
                                Intent(
                                    activity, Connection_Timer::class.java
                                )
                            )
                        }
                    }.start()



                    btnDNSTTStart!!.setBackgroundResource(R.drawable.connected);
                    spConnectionMethod!!.isEnabled = false
                    etDtt!!.isEnabled = false
                    rgDNSTT!!.isEnabled = false
                    rbUDP!!.isEnabled = false
                    rbDOT!!.isEnabled = false
                    rbDOH!!.isEnabled = false
                    if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                        timer_layout!!.visibility = GONE
                    } else {
                        timer_layout!!.visibility = VISIBLE
                    }

                } else {


                    val builder = AlertDialog.Builder(requireContext(), R.style.disconnectdialog)
                    builder.setCancelable(false)
                    val alertView =
                        View.inflate(requireContext(), R.layout.dialog_medium_banner_layout, null)
                    //  showNativeAd(alertView)
                    builder.setTitle("Do you want to disconnect?").setView(alertView)
                    builder.setPositiveButton("Yes") { dialogInterface: DialogInterface?, i: Int ->


                        val mainActivity: MainActivity = activity as MainActivity
                        DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                            DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT, false
                        )

                        val i = Intent(
                            context, DnsService::class.java
                        )
                        requireContext().stopService(i)
                        mainActivity.stopVpn()




                        btnDNSTTStart!!.setBackgroundResource(R.drawable.disconnected);
                        spConnectionMethod!!.isEnabled = true
                        etDtt!!.isEnabled = true
                        rgDNSTT!!.isEnabled = true
                        rbUDP!!.isEnabled = true
                        rbDOT!!.isEnabled = true
                        rbDOH!!.isEnabled = true
                        timer_layout!!.visibility = GONE


                        (activity as MainActivity).stopService(
                            Intent(
                                activity, Connection_Timer::class.java
                            )
                        )

                        timeLeft_val!!.text =
                            "00:" + getBestServer_after_calculation(choose_Server_preference)!!.timer_val.toString() + ":00"


                    }.setNegativeButton(
                        "No"
                    ) { dialogInterface, i ->
                        //   loadNativeAd()
                    }
                    builder.show()


                }
            } else {
                Toast.makeText(requireActivity(), "Internet is required", Toast.LENGTH_LONG).show()
            }
        }



        btnStartV2RayCC!!.setOnClickListener {
            if (isNetworkAvailable(requireActivity())) {
                val mainActivity: MainActivity = activity as MainActivity
                if (!DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY_DEFAULT
                    )
                ) {
                    importClipboard()
                    Observable.timer(500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe {
                            DmSharedPreferences.getInstance(requireContext())!!
                                .putBoolean(DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY, true)


                            mainActivity.startVpn()
                            btnStartV2RayCC!!.setImageResource(R.drawable.connected)
                            if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                                timer_layout!!.visibility = GONE
                            } else {
                                timer_layout!!.visibility = VISIBLE
                            }


                            if (timer_connecting != null) timer_connecting!!.cancel()

                            timer_connecting = object : CountDownTimer(10000, 100) {
                                override fun onTick(millisUntilFinished: Long) {
                                    if (isOven_Vpn_ConnectionActive()) {

                                        timer_connecting?.cancel()

                                        if (activity != null && isAdded) {
//                                        val intent = Intent(activity, Connection_Timer::class.java)
//                                        activity!!.startService(intent)
                                            start_service_to_calculate()

                                        }
//                                    start_service_to_calculate()
                                    }
                                }

                                override fun onFinish() {
                                    (activity as MainActivity).stopService(
                                        Intent(
                                            activity, Connection_Timer::class.java
                                        )
                                    )

                                }
                            }.start()
                        }
                } else {
                    val builder = AlertDialog.Builder(requireContext(), R.style.disconnectdialog)
                    builder.setCancelable(false)
                    val alertView =
                        View.inflate(requireContext(), R.layout.dialog_medium_banner_layout, null)
                    //  showNativeAd(alertView)
                    builder.setTitle("Do you want to disconnect?").setView(alertView)
                    builder.setPositiveButton("Yes") { dialogInterface: DialogInterface?, i: Int ->
                        DmSharedPreferences.getInstance(requireContext())!!
                            .putBoolean(DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY, false)

                        (activity as MainActivity).stopService(
                            Intent(
                                activity, Connection_Timer::class.java
                            )
                        )
                        mainActivity.stopVpn()
                        btnStartV2RayCC!!.setImageResource(R.drawable.disconnected)
                        timer_layout!!.visibility = GONE
                        timeLeft_val!!.text =
                            "00:" + getBestServer_after_calculation(choose_Server_preference)!!.timer_val.toString() + ":00"

                    }.setNegativeButton(
                        "No"
                    ) { dialogInterface, i ->
                        //   loadNativeAd()
                    }
                    builder.show()
                }
            } else {
                Toast.makeText(requireActivity(), "Internet is required", Toast.LENGTH_LONG).show()
            }
        }


        moreTime!!.setOnClickListener {


            if (Application.mRewardedAd != null) {
                Application.mRewardedAd?.show(requireActivity(), OnUserEarnedRewardListener() {
                    fun onUserEarnedReward(rewardItem: RewardItem) {
                        var rewardAmount = rewardItem.amount
                        var rewardType = rewardItem.type
                        increase_time_and_update_view()
                        //   Log.d(TAG, "User earned the reward.")
                    }
                })
            } else {
                TastyToast.makeText(
                    requireContext(),
                    "Ad Not Loaded Yet",
                    TastyToast.LENGTH_LONG,
                    TastyToast.WARNING
                ).show()
            }


            //   increase_time_and_update_view()

        }


        v2ray_tcp!!.setOnClickListener {

            v2rayportudp = "false"
            v2ray_tcp!!.setBackgroundResource(R.drawable.selected_port_bg)
            v2ray_udp!!.setBackgroundResource(R.drawable.port_bg)
        }

        v2ray_udp!!.setOnClickListener {
            v2rayportudp = "true"
            v2ray_tcp!!.setBackgroundResource(R.drawable.port_bg)
            v2ray_udp!!.setBackgroundResource(R.drawable.selected_port_bg)
        }

        if (!fileImportedPath.isNullOrEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                TastyToast.makeText(
                    requireContext(),
                    getString(string.MSG_OPEN_FILE_MANUALLY),
                    TastyToast.LENGTH_LONG,
                    TastyToast.WARNING
                ).show()
            } else {
                readConfigurationFile(fileImportedPath)
                TastyToast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.imported_configuration),
                    Toast.LENGTH_LONG,
                    TastyToast.WARNING
                ).show()
                requireActivity().finish()
            }
        }
        blockScreenPerImport()

        return viewContent
    }

    @SuppressLint("SetTextI18n")
    fun set_initial_value_time() {
        if (!isOven_Vpn_ConnectionActive()) {
            sharedPreferences = requireActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE)
            if (sharedPreferences != null) {
                timeLeft_val!!.text =
                    "00:" + getBestServer_after_calculation(choose_Server_preference)!!.timer_val.toString() + ":00"
            }
        }
    }

    fun getAllServers_from_cache(): ArrayList<api_response>? {
        try {
            val gson = Gson()
            if (choose_Server_preference != null) {
                val json: String? = choose_Server_preference!!.getString("list_saved_cache", null)
                if (json != null) {
                    if (!json.isEmpty() || json != "") {
                        return gson.fromJson<java.util.ArrayList<api_response>>(
                            json, Api::class.java
                        )
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun openVpnConnection() {

        val mainActivity: MainActivity = activity as MainActivity
        if (!DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN_DEFAULT
            )
        ) {
            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN, true
            )
            createOpenVpnCert()
            //  btnFastDNSStart!!.text = requireContext().resources.getString(string.payload_stop)
            btnFastDNSStart!!.setImageResource(R.drawable.connected)
            if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                timer_layout!!.visibility = GONE
            } else {
                timer_layout!!.visibility = VISIBLE
            }
            spConnectionMethod!!.isEnabled = false
            open_server_list!!.isEnabled = false


        } else {
            //    mConn.service.stopVPN()
            // loadNativeAd()
            requireActivity().stopService(Intent(activity, Connection_Timer::class.java))

            val intent = Intent(context, DisconnectVPN::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            // LINEA PARA QUE EL OPEN VPN NO INICIE SOLO EL SERVICIO
            startActivity(intent)

            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN, false
            )
//                disconnectOpenVpn()

            timeLeft_val!!.text =
                "00:" + getBestServer_after_calculation(choose_Server_preference)!!.timer_val.toString() + ":00"
            btnFastDNSStart!!.setImageResource(R.drawable.disconnected)
            //    btnFastDNSStart!!.text = requireContext().resources.getString(string.payload_start)
            timer_layout!!.visibility = GONE
            spConnectionMethod!!.isEnabled = true
            open_server_list!!.isEnabled = true

        }
    }

    private fun createOpenVpnCert() {
        val serverIP = getConfiguredServer(requireContext()).split(':')[0]


        val harcode_data = "##############################################################\n" +
                "client\n" +
                "dev tun\n" +
                "proto udp\n" +
                "remote " + " " + getBestServer_after_calculation(choose_Server_preference)!!.ip + " " + 443 + "\n" +
                "connect-retry 1 1\n" +
                "connect-timeout 10\n" +
                "ping-restart 25\n" +
                "nobind\n" +
                "persist-key\n" +
                "persist-tun\n" +
                "shaper 12000000\n" +
                "remote-cert-tls server\n" +
                "auth SHA512\n" +
                "cipher AES-128-OFB\n" +
                "dhcp-option DNS 8.8.8.8\n" +
                "dhcp-option DNS 8.8.4.4\n" +
                "dhcp-option DNS 1.1.1.1\n" +
                "register-dns\n" +
                "#############################################################\n" +
                "<ca>\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDQjCCAiqgAwIBAgIUHLSt6qW3f+z3lxscQdoAs77tac0wDQYJKoZIhvcNAQEL\n" +
                "BQAwEzERMA8GA1UEAwwIQ2hhbmdlTWUwHhcNMjExMTE1MjMyODE4WhcNMzExMTEz\n" +
                "MjMyODE4WjATMREwDwYDVQQDDAhDaGFuZ2VNZTCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
                "ggEPADCCAQoCggEBAMpmoKvLXtF9XWZVeQhm0XlvCq0ez18l5PFsoIs2HWhbsOkC\n" +
                "btwiVKCMAwYav4sAW2Op/w1nmzs1BLgLjVP4v1B/efYDFFy8eJuDKWdObw/uNqxY\n" +
                "9OYQXcLiDhNrE482xB4G9VsvAj7UgzdQPYYA9IkaMLC21oPfi5MHF48pmzdea58b\n" +
                "xYoWuGLenFOyh45FQa1s7xafUEUJTZuhoQ+FPm/OrzvBTzyL0aHcIZu7g1BRicgb\n" +
                "BnQSD6TsspttN9scLqG/ev4wVtkuPCeXq0ri7F9sSJp8RyOBBqRps0N0tohtEJ2c\n" +
                "CCeLKjCng44ePH+Pef+ppLL99ct/b5Np4eKQp8sCAwEAAaOBjTCBijAdBgNVHQ4E\n" +
                "FgQUClXaJR9/sA27bW+/3MuzmWsrhHMwTgYDVR0jBEcwRYAUClXaJR9/sA27bW+/\n" +
                "3MuzmWsrhHOhF6QVMBMxETAPBgNVBAMMCENoYW5nZU1lghQctK3qpbd/7PeXGxxB\n" +
                "2gCzvu1pzTAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBBjANBgkqhkiG9w0BAQsF\n" +
                "AAOCAQEAM8R30jEIHJ0o4JZuHbS3dLSasn92HG6EXSK6meodHwtXx1jS6A6kOEJI\n" +
                "fg7nciEDgA6c3d2YCtQrvCkzjKpPyGWYSR0BPN7xfhmG57tClz3i87gV+OAT/Igg\n" +
                "SV+v1x/fRgm5igUL0rhUJGOGqkpynUODircdQ8ecZIYQ20zbk3sHTb+ybRN2J5Aj\n" +
                "0VCnDp6pt1Whw7W3/XCx8JOGXqPN8Cm8mYeaDbODFVqkqtLocX7RFJjBg/k9VJQi\n" +
                "HDH2eiEibmLqx+J6NvrCyRJIhEANVov6vrWbK9fu7NmTF1roHtmQc4jy/k/Snrh+\n" +
                "G9iEieYmoEQo1UKe7h0w1dp0PDTrEQ==\n" +
                "-----END CERTIFICATE-----\n" +
                "</ca>\n" +
                "<cert>\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDTzCCAjegAwIBAgIRAN8krZG+6sHE7mJdZQJ2eXswDQYJKoZIhvcNAQELBQAw\n" +
                "EzERMA8GA1UEAwwIQ2hhbmdlTWUwHhcNMjExMTE1MjMyODE4WhcNMzExMTEzMjMy\n" +
                "ODE4WjARMQ8wDQYDVQQDDAZjbGllbnQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw\n" +
                "ggEKAoIBAQDK8nID29CTFMoriNPqWaW8E/zzKqBP2ie1oyxskIt9TxJEfp/+jNaV\n" +
                "uDY3kX4OhusDvWNBYjOQpTx3IjgW0OaJfedeHtKo+vsJrzCn8PV354XKRfJceWVt\n" +
                "5N8GRHErcm8sNNQTXERL6POnZdvVBMAxBFHx2TvF5iQ/S1PpLThBuz6VX7OBX424\n" +
                "4rzIX51HfLsoKCzEa4okWgMU3Uz3gVW2x2j2i1BgqnxkYXfEDUlrg5N96drBgdxX\n" +
                "xQkgFN/0d9AZFgyFWrzMIxfo2qbOF5DGZ54d+G5+qu8VRRwE7Sew68+5ic3tNAPx\n" +
                "4lJPmlIQ1LCI9/sLs7lEfePfh6HQh+OhAgMBAAGjgZ8wgZwwCQYDVR0TBAIwADAd\n" +
                "BgNVHQ4EFgQUD+cFgn4YLXTNLnmXWYLGyn69uUswTgYDVR0jBEcwRYAUClXaJR9/\n" +
                "sA27bW+/3MuzmWsrhHOhF6QVMBMxETAPBgNVBAMMCENoYW5nZU1lghQctK3qpbd/\n" +
                "7PeXGxxB2gCzvu1pzTATBgNVHSUEDDAKBggrBgEFBQcDAjALBgNVHQ8EBAMCB4Aw\n" +
                "DQYJKoZIhvcNAQELBQADggEBAA2M6WhfrFX1gsZSHQWE8JwZz3FaWSovBVSCygG2\n" +
                "C45ll0gvi2ZM+Q4/93zVY1iTCxv3CskppqRHWdxh9aPQApx9CwAk6q/q7/51IrpK\n" +
                "UVGF9EFQtrI9liZK1nG7LZk8hImKK4d2zOsc52e5iJAfql8lSHSbknLiaQOuR8Mv\n" +
                "T5TvLKrxiK5gvLbfYxH1yxlIsMxID+Sj9qKugm5NBlxG6poZRgVIIxWkogDN2V7P\n" +
                "Wt4aeovGmTSpWZdvnTb9GTkAY4v/j5Ff8eyQ2jyya1Zt50bT/LV0ta5EIJWPRm7p\n" +
                "W5KJtqWn1ObYmm2Jw6A5krJmdhvbJRe5b9RyipsMrVCaWSs=\n" +
                "-----END CERTIFICATE-----\n" +
                "</cert>\n" +
                "<key>\n" +
                "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDK8nID29CTFMor\n" +
                "iNPqWaW8E/zzKqBP2ie1oyxskIt9TxJEfp/+jNaVuDY3kX4OhusDvWNBYjOQpTx3\n" +
                "IjgW0OaJfedeHtKo+vsJrzCn8PV354XKRfJceWVt5N8GRHErcm8sNNQTXERL6POn\n" +
                "ZdvVBMAxBFHx2TvF5iQ/S1PpLThBuz6VX7OBX4244rzIX51HfLsoKCzEa4okWgMU\n" +
                "3Uz3gVW2x2j2i1BgqnxkYXfEDUlrg5N96drBgdxXxQkgFN/0d9AZFgyFWrzMIxfo\n" +
                "2qbOF5DGZ54d+G5+qu8VRRwE7Sew68+5ic3tNAPx4lJPmlIQ1LCI9/sLs7lEfePf\n" +
                "h6HQh+OhAgMBAAECggEBAMCUUabhtslrB/FJtLYD/aY8XWKuB1Pe0qGkxmn6u7Lc\n" +
                "Dpyaf94/7PxDwob2c+T7GBYDaCVKN15Bvd/aw9i6wJiltaB1pTceeosVCBnFKFlw\n" +
                "hF+OAk7ID3y5mvzKg41T2LTdKIV6n4Bc/KDTokhAatPYop3ZosPHAIxjQavZ8jFD\n" +
                "97bQlL+g53rbqMu+NrZXhy9LO2bF90aZpmiPniNeIA1BpvDStcD4dxJvAb7KxUCJ\n" +
                "Vu4rnSLDVAU4hPNFUw7WoYs+xaGu4MSwPobIV+F1hKTwIGYh0kRqJ4v8ibHBVb9b\n" +
                "qK8Y3S93qHoMWDrDBAjaAKxIvAgrPnzVgWG28kLh8IUCgYEA+pHhgNTjS8rYN+5Q\n" +
                "EK3Z7GddBOo/lURtZBrbp6pRLQTS4qx8JG4CHhmofNvnKrYChr0yncv+fyKWqKEj\n" +
                "/hzl2cIsrRA97xoWd9cNTUE3YkREW3I4a0ktevcJ7+a9AFOGGPMb7KO16ttU1tag\n" +
                "iNLEA20K+ktCeYUFFBaFB69guuMCgYEAz1hcfWdPLn0ZxZ5wA1Vt0GAvrFhpD7lk\n" +
                "8h7WJNnPa3zVSsApoUsuiFeZPtJHr4JYm4j45AIteoofYUAC2WtIgZNxddD7ar27\n" +
                "WkdQFzjirQOluhSus3nOM+NVB38ZJhcJZcSQ+dtkzF7Sh/QLlBD17vFdIWqatSEP\n" +
                "uaVV2ptQGqsCgYAMebzvjCnw+R2A02W7HtzNz2urNfqKdQi5RjkPhQRbqHTTgmD/\n" +
                "81Y5p29jIBPHZZ6ElODzSFfYfqFfrf47xbaP/AH7b+aVhAgAWX25Dfky9FEu+GXk\n" +
                "uZUBHVGqVyOr43y2u0v1oUUKd3cm5zLA/TVzMjestJiPslbJZEGlW08GnwKBgHKC\n" +
                "mEHhpkdEa9FKGQ6eRZGJLYr/ILbAatGU2itr5zOzs5Ae5F7hyIcf6dBaDaEOWaU8\n" +
                "VkiGM1uzoHTzBx/QMjDtnwG0MsXWlvva2o6YqgdnNkW4WmtgX9MkP7duKBNzMYt0\n" +
                "raRv2BFOZPMUgrh5TXly7z4fQ+W4ppieiPW8RNcVAoGBAMH5im+iaUVSwDD8O8R5\n" +
                "q2EiaF5S8ZZWplYw0fwgdfLJEJ4ShPLp0UoOG5txQIfFCc0Bj3RAjmgGyv2EkUvN\n" +
                "tu0/j+C4mri2oA8cNjnw4Blevsdit8Z1q8Z4ig49ygTp26Ft25Sx3N3RKzEIt8Z7\n" +
                "rsQtiJDCHDWMaHiA7eLarVLI\n" +
                "-----END PRIVATE KEY-----\n" +
                "</key>\n" +
                "<tls-crypt>\n" +
                "-----BEGIN OpenVPN Static key V1-----\n" +
                "e4a255ce4222d17ccc62b45d3780f2d9\n" +
                "427ced706c8a71280d9a0f2ebddb25a9\n" +
                "25e754d5c9baccd01d191d9875e6165e\n" +
                "06ed7cb2fd0807ff201f904709c06a9e\n" +
                "aac6127074c945f8307c0809f4f0b47a\n" +
                "8e659f7e8e386a454f6d493cf57f1527\n" +
                "c9652589afbd37dc2ea953f884f899ea\n" +
                "dd61a595bbf1466148be9571e8825f4e\n" +
                "8352df8b386fdd7225f60f23f51514df\n" +
                "5cba3f9402a1f5d189f358a45564a19c\n" +
                "15fe4a9fbf21bb92c4d41efd8ff5dc67\n" +
                "ff988d8362b86ad8ab213be54579b36a\n" +
                "2b957767dc50d1f8576e6b2251afdebb\n" +
                "a1ad02250bede2d1bb779a88fcf8828c\n" +
                "a138e48c0a6b391580525165739a5c81\n" +
                "8bd037f4e1890a30014eee31ff8cefaf\n" +
                "-----END OpenVPN Static key V1-----\n" +
                "</tls-crypt>"


        val vpnProfile: VpnProfile = ClientManager.generateOvpn(
            context, harcode_data, getBestServer_after_calculation(choose_Server_preference)!!.city
        )
        // LINEA PARA QUE EL OPEN VPN NO INICIE SOLO EL SERVICIO
        ClientManager.startVPN(context, vpnProfile)


        if (timer_connecting != null) timer_connecting!!.cancel()

        timer_connecting = object : CountDownTimer(20000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (isOven_Vpn_ConnectionActive()) {

                    timer_connecting?.cancel()

                    if (activity != null && isAdded) {
//                        val intent_service = Intent(activity, Connection_Timer::class.java)
//                        activity!!.startService(intent_service)
                        start_service_to_calculate()
                    }
//                    start_service_to_calculate()
                }
            }

            override fun onFinish() {
                (activity as MainActivity).stopService(
                    Intent(
                        activity, Connection_Timer::class.java
                    )
                )

            }
        }.start()
    }


    private fun createOpenVpnCertFastDNS() {
        val serverIP = getBestServer_after_calculation(choose_Server_preference)!!.ip


        val fast_DNS_DATA = "client\n" +
                "dev tun\n" +
                "proto udp\n" +
                "remote 127.0.0.1 2323\n" +
                "resolv-retry infinite\n" +
                "nobind\n" +
                "persist-key\n" +
                "persist-tun\n" +
                "remote-cert-tls server\n" +
                "auth SHA512\n" +
                "cipher AES-128-OFB\n" +
                "ignore-unknown-option block-outside-dns\n" +
                "block-outside-dns\n" +
                "verb 3\n" +
                "route" + " " + serverIP + " 255.255.255.255 net_gateway\n" +
                "<ca>\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDQjCCAiqgAwIBAgIUHLSt6qW3f+z3lxscQdoAs77tac0wDQYJKoZIhvcNAQEL\n" +
                "BQAwEzERMA8GA1UEAwwIQ2hhbmdlTWUwHhcNMjExMTE1MjMyODE4WhcNMzExMTEz\n" +
                "MjMyODE4WjATMREwDwYDVQQDDAhDaGFuZ2VNZTCCASIwDQYJKoZIhvcNAQEBBQAD\n" +
                "ggEPADCCAQoCggEBAMpmoKvLXtF9XWZVeQhm0XlvCq0ez18l5PFsoIs2HWhbsOkC\n" +
                "btwiVKCMAwYav4sAW2Op/w1nmzs1BLgLjVP4v1B/efYDFFy8eJuDKWdObw/uNqxY\n" +
                "9OYQXcLiDhNrE482xB4G9VsvAj7UgzdQPYYA9IkaMLC21oPfi5MHF48pmzdea58b\n" +
                "xYoWuGLenFOyh45FQa1s7xafUEUJTZuhoQ+FPm/OrzvBTzyL0aHcIZu7g1BRicgb\n" +
                "BnQSD6TsspttN9scLqG/ev4wVtkuPCeXq0ri7F9sSJp8RyOBBqRps0N0tohtEJ2c\n" +
                "CCeLKjCng44ePH+Pef+ppLL99ct/b5Np4eKQp8sCAwEAAaOBjTCBijAdBgNVHQ4E\n" +
                "FgQUClXaJR9/sA27bW+/3MuzmWsrhHMwTgYDVR0jBEcwRYAUClXaJR9/sA27bW+/\n" +
                "3MuzmWsrhHOhF6QVMBMxETAPBgNVBAMMCENoYW5nZU1lghQctK3qpbd/7PeXGxxB\n" +
                "2gCzvu1pzTAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBBjANBgkqhkiG9w0BAQsF\n" +
                "AAOCAQEAM8R30jEIHJ0o4JZuHbS3dLSasn92HG6EXSK6meodHwtXx1jS6A6kOEJI\n" +
                "fg7nciEDgA6c3d2YCtQrvCkzjKpPyGWYSR0BPN7xfhmG57tClz3i87gV+OAT/Igg\n" +
                "SV+v1x/fRgm5igUL0rhUJGOGqkpynUODircdQ8ecZIYQ20zbk3sHTb+ybRN2J5Aj\n" +
                "0VCnDp6pt1Whw7W3/XCx8JOGXqPN8Cm8mYeaDbODFVqkqtLocX7RFJjBg/k9VJQi\n" +
                "HDH2eiEibmLqx+J6NvrCyRJIhEANVov6vrWbK9fu7NmTF1roHtmQc4jy/k/Snrh+\n" +
                "G9iEieYmoEQo1UKe7h0w1dp0PDTrEQ==\n" +
                "-----END CERTIFICATE-----\n" +
                "</ca>\n" +
                "<cert>\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDTzCCAjegAwIBAgIRAN8krZG+6sHE7mJdZQJ2eXswDQYJKoZIhvcNAQELBQAw\n" +
                "EzERMA8GA1UEAwwIQ2hhbmdlTWUwHhcNMjExMTE1MjMyODE4WhcNMzExMTEzMjMy\n" +
                "ODE4WjARMQ8wDQYDVQQDDAZjbGllbnQwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw\n" +
                "ggEKAoIBAQDK8nID29CTFMoriNPqWaW8E/zzKqBP2ie1oyxskIt9TxJEfp/+jNaV\n" +
                "uDY3kX4OhusDvWNBYjOQpTx3IjgW0OaJfedeHtKo+vsJrzCn8PV354XKRfJceWVt\n" +
                "5N8GRHErcm8sNNQTXERL6POnZdvVBMAxBFHx2TvF5iQ/S1PpLThBuz6VX7OBX424\n" +
                "4rzIX51HfLsoKCzEa4okWgMU3Uz3gVW2x2j2i1BgqnxkYXfEDUlrg5N96drBgdxX\n" +
                "xQkgFN/0d9AZFgyFWrzMIxfo2qbOF5DGZ54d+G5+qu8VRRwE7Sew68+5ic3tNAPx\n" +
                "4lJPmlIQ1LCI9/sLs7lEfePfh6HQh+OhAgMBAAGjgZ8wgZwwCQYDVR0TBAIwADAd\n" +
                "BgNVHQ4EFgQUD+cFgn4YLXTNLnmXWYLGyn69uUswTgYDVR0jBEcwRYAUClXaJR9/\n" +
                "sA27bW+/3MuzmWsrhHOhF6QVMBMxETAPBgNVBAMMCENoYW5nZU1lghQctK3qpbd/\n" +
                "7PeXGxxB2gCzvu1pzTATBgNVHSUEDDAKBggrBgEFBQcDAjALBgNVHQ8EBAMCB4Aw\n" +
                "DQYJKoZIhvcNAQELBQADggEBAA2M6WhfrFX1gsZSHQWE8JwZz3FaWSovBVSCygG2\n" +
                "C45ll0gvi2ZM+Q4/93zVY1iTCxv3CskppqRHWdxh9aPQApx9CwAk6q/q7/51IrpK\n" +
                "UVGF9EFQtrI9liZK1nG7LZk8hImKK4d2zOsc52e5iJAfql8lSHSbknLiaQOuR8Mv\n" +
                "T5TvLKrxiK5gvLbfYxH1yxlIsMxID+Sj9qKugm5NBlxG6poZRgVIIxWkogDN2V7P\n" +
                "Wt4aeovGmTSpWZdvnTb9GTkAY4v/j5Ff8eyQ2jyya1Zt50bT/LV0ta5EIJWPRm7p\n" +
                "W5KJtqWn1ObYmm2Jw6A5krJmdhvbJRe5b9RyipsMrVCaWSs=\n" +
                "-----END CERTIFICATE-----\n" +
                "</cert>\n" +
                "<key>\n" +
                "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDK8nID29CTFMor\n" +
                "iNPqWaW8E/zzKqBP2ie1oyxskIt9TxJEfp/+jNaVuDY3kX4OhusDvWNBYjOQpTx3\n" +
                "IjgW0OaJfedeHtKo+vsJrzCn8PV354XKRfJceWVt5N8GRHErcm8sNNQTXERL6POn\n" +
                "ZdvVBMAxBFHx2TvF5iQ/S1PpLThBuz6VX7OBX4244rzIX51HfLsoKCzEa4okWgMU\n" +
                "3Uz3gVW2x2j2i1BgqnxkYXfEDUlrg5N96drBgdxXxQkgFN/0d9AZFgyFWrzMIxfo\n" +
                "2qbOF5DGZ54d+G5+qu8VRRwE7Sew68+5ic3tNAPx4lJPmlIQ1LCI9/sLs7lEfePf\n" +
                "h6HQh+OhAgMBAAECggEBAMCUUabhtslrB/FJtLYD/aY8XWKuB1Pe0qGkxmn6u7Lc\n" +
                "Dpyaf94/7PxDwob2c+T7GBYDaCVKN15Bvd/aw9i6wJiltaB1pTceeosVCBnFKFlw\n" +
                "hF+OAk7ID3y5mvzKg41T2LTdKIV6n4Bc/KDTokhAatPYop3ZosPHAIxjQavZ8jFD\n" +
                "97bQlL+g53rbqMu+NrZXhy9LO2bF90aZpmiPniNeIA1BpvDStcD4dxJvAb7KxUCJ\n" +
                "Vu4rnSLDVAU4hPNFUw7WoYs+xaGu4MSwPobIV+F1hKTwIGYh0kRqJ4v8ibHBVb9b\n" +
                "qK8Y3S93qHoMWDrDBAjaAKxIvAgrPnzVgWG28kLh8IUCgYEA+pHhgNTjS8rYN+5Q\n" +
                "EK3Z7GddBOo/lURtZBrbp6pRLQTS4qx8JG4CHhmofNvnKrYChr0yncv+fyKWqKEj\n" +
                "/hzl2cIsrRA97xoWd9cNTUE3YkREW3I4a0ktevcJ7+a9AFOGGPMb7KO16ttU1tag\n" +
                "iNLEA20K+ktCeYUFFBaFB69guuMCgYEAz1hcfWdPLn0ZxZ5wA1Vt0GAvrFhpD7lk\n" +
                "8h7WJNnPa3zVSsApoUsuiFeZPtJHr4JYm4j45AIteoofYUAC2WtIgZNxddD7ar27\n" +
                "WkdQFzjirQOluhSus3nOM+NVB38ZJhcJZcSQ+dtkzF7Sh/QLlBD17vFdIWqatSEP\n" +
                "uaVV2ptQGqsCgYAMebzvjCnw+R2A02W7HtzNz2urNfqKdQi5RjkPhQRbqHTTgmD/\n" +
                "81Y5p29jIBPHZZ6ElODzSFfYfqFfrf47xbaP/AH7b+aVhAgAWX25Dfky9FEu+GXk\n" +
                "uZUBHVGqVyOr43y2u0v1oUUKd3cm5zLA/TVzMjestJiPslbJZEGlW08GnwKBgHKC\n" +
                "mEHhpkdEa9FKGQ6eRZGJLYr/ILbAatGU2itr5zOzs5Ae5F7hyIcf6dBaDaEOWaU8\n" +
                "VkiGM1uzoHTzBx/QMjDtnwG0MsXWlvva2o6YqgdnNkW4WmtgX9MkP7duKBNzMYt0\n" +
                "raRv2BFOZPMUgrh5TXly7z4fQ+W4ppieiPW8RNcVAoGBAMH5im+iaUVSwDD8O8R5\n" +
                "q2EiaF5S8ZZWplYw0fwgdfLJEJ4ShPLp0UoOG5txQIfFCc0Bj3RAjmgGyv2EkUvN\n" +
                "tu0/j+C4mri2oA8cNjnw4Blevsdit8Z1q8Z4ig49ygTp26Ft25Sx3N3RKzEIt8Z7\n" +
                "rsQtiJDCHDWMaHiA7eLarVLI\n" +
                "-----END PRIVATE KEY-----\n" +
                "</key>\n" +
                "<tls-crypt>\n" +
                "-----BEGIN OpenVPN Static key V1-----\n" +
                "e4a255ce4222d17ccc62b45d3780f2d9\n" +
                "427ced706c8a71280d9a0f2ebddb25a9\n" +
                "25e754d5c9baccd01d191d9875e6165e\n" +
                "06ed7cb2fd0807ff201f904709c06a9e\n" +
                "aac6127074c945f8307c0809f4f0b47a\n" +
                "8e659f7e8e386a454f6d493cf57f1527\n" +
                "c9652589afbd37dc2ea953f884f899ea\n" +
                "dd61a595bbf1466148be9571e8825f4e\n" +
                "8352df8b386fdd7225f60f23f51514df\n" +
                "5cba3f9402a1f5d189f358a45564a19c\n" +
                "15fe4a9fbf21bb92c4d41efd8ff5dc67\n" +
                "ff988d8362b86ad8ab213be54579b36a\n" +
                "2b957767dc50d1f8576e6b2251afdebb\n" +
                "a1ad02250bede2d1bb779a88fcf8828c\n" +
                "a138e48c0a6b391580525165739a5c81\n" +
                "8bd037f4e1890a30014eee31ff8cefaf\n" +
                "-----END OpenVPN Static key V1-----\n" +
                "</tls-crypt>\n"


        val vpnProfile: VpnProfile = ClientManager.generateOvpn(
            context, fast_DNS_DATA, getBestServer_after_calculation(choose_Server_preference)!!.city
        )
        // LINEA PARA QUE EL OPEN VPN NO INICIE SOLO EL SERVICIO
        ClientManager.startVPN(context, vpnProfile)

        if (timer_connecting != null) timer_connecting!!.cancel()

        timer_connecting = object : CountDownTimer(20000, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (isOven_Vpn_ConnectionActive()) {

                    timer_connecting?.cancel()

                    if (activity != null && isAdded) {
//                        val intent_service = Intent(activity, Connection_Timer::class.java)
//                        activity!!.startService(intent_service)
                        start_service_to_calculate()
                    }
//                    start_service_to_calculate()
                }
            }

            override fun onFinish() {
                (activity as MainActivity).stopService(
                    Intent(
                        activity, Connection_Timer::class.java
                    )
                )

            }
        }.start()
    }

    protected var openvpn_service: OpenVPNService? = null
    private fun disconnectOpenVpn() {

        /*  ProfileManager.setConntectedVpnProfileDisconnected(activity)
          if (openvpn_service != null && openvpn_service!!.getManagement() != null) {
              openvpn_service!!.getManagement().stopVPN(false)
          }*/
        requireActivity().stopService(Intent(activity, Connection_Timer::class.java))

        val intent = Intent(context, DisconnectVPN::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        // LINEA PARA QUE EL OPEN VPN NO INICIE SOLO EL SERVICIO
        startActivity(intent)


    }


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (isOven_Vpn_ConnectionActive()) {
                if (intent.extras != null) {
                    val millisUntilFinished = intent.getLongExtra("countdown", 0)
                    val mainActivity: MainActivity = activity as MainActivity

                    e("initial_start_3", "" + millisUntilFinished)
                    if (millisUntilFinished == 0L) {
                        e("initial_start_10", "" + millisUntilFinished)

                        val intent_service = Intent(activity, Connection_Timer::class.java)
                        activity!!.stopService(intent_service)
                        try {
                            /* if (isOpenVpnConnection(requireActivity())) {
                                 disconnectOpenVpn()
                                 timeLeft_val!!.text =
                                     "00:" + getBestServer_after_calculation(choose_Server_preference)!!.timer_val.toString() + ":00"
                                 moreTime!!.isEnabled = true
                                 moreTime!!.isClickable = true
                             }*/

                            if (spConnectionMethod != null) {

                                var passStr: String? = null

                                if (spConnectionMethod!!.selectedItem == null) {
                                    spConnectionMethod!!.setSelection(
                                        adapter!!.getPosition(
                                            getString(string.fastdns_connection)
                                        )
                                    )
                                    passStr = spConnectionMethod!!.selectedItem.toString()
                                } else {
                                    passStr = spConnectionMethod!!.selectedItem.toString()
                                }

                                if (passStr == "FastDNS") {
                                    btnFastDNSStart!!.setImageResource(R.drawable.disconnected)

                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY, false
                                    )

                                    val intent = Intent(context, DisconnectVPN::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    startActivity(intent)
                                }
                                if (passStr == "DNSTT") {
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY, false
                                    )

                                    btnDNSTTStart!!.setImageResource(R.drawable.disconnected)
                                    val i = Intent(requireActivity(), DnsService::class.java)
                                    requireActivity().stopService(i)
                                    mainActivity.stopVpn()
                                }
                                if (passStr == "OpenVPN") {
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY, false
                                    )

                                    btnFastDNSStart!!.setImageResource(R.drawable.disconnected)
                                    val intent = Intent(context, DisconnectVPN::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    startActivity(intent)
                                }
                                if (passStr == "V2Ray") {
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_OPEN_VPN, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_DNS_TT, false
                                    )
                                    DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_V2RAY, false
                                    )

                                    btnStartV2RayCC!!.setImageResource(R.drawable.disconnected)
                                    mainActivity.stopVpn()
                                }
                            }
                        } catch (e: Exception) {

                        }
                    } else {
                        updateCountDownText(millisUntilFinished)
                        try {
                            moreTime!!.isEnabled = false
                            moreTime!!.isClickable = false
                        } catch (e: Exception) {
                        }
                    }
                }
            } else {
                requireActivity().stopService(Intent(activity, Connection_Timer::class.java))
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun increase_time_and_update_view() {


        val df = SimpleDateFormat("HH:mm:ss")
        try {
            var d: Date? = null
            d = df.parse(timeLeft_val!!.text.toString())
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.MINUTE, 10)
            val newTime = df.format(cal.time)
            timeLeft_val!!.text = newTime
        } catch (e: ParseException) {

            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun start_service_to_calculate() {

        if (timeLeft_val!!.text.toString().contains("00:00:00")) {
            timeLeft_val!!.text = "00:1:00"
        }

        val string_date = timeLeft_val!!.text.toString()
        e("initial_start_2", "" + string_date)
        val f = SimpleDateFormat("HH:mm:ss")
        f.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val d = f.parse(string_date)
            if (d != null) {
                val milliseconds = d.time
                e("milli_times", "" + milliseconds)

                val intent_service =
                    Intent(requireActivity(), Connection_Timer::class.java).putExtra(
                        "milliseconds", milliseconds
                    )
                requireActivity().startService(intent_service)

            }
        } catch (e: java.lang.Exception) {

        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateCountDownText(mTimeLeftInMillis: Long) {
        val hours = (mTimeLeftInMillis / 1000).toInt() / 3600
        val minutes = (mTimeLeftInMillis / 1000 % 3600).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted: String =
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        e("timeLeftFormatted_val", timeLeftFormatted)
        timeLeft_val!!.text = timeLeftFormatted
    }

    private fun fastDnsConnection() {
        val mainActivity: MainActivity = activity as MainActivity
        if (!DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS_DEFAULT
            )
        ) {
            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS, true
            )

            val serverIP = getBestServer_after_calculation(choose_Server_preference)!!.ip


            val i = Intent(
                context, DnsService::class.java
            )
            i.putExtra("host", serverIP)
            i.putExtra("extra", "53") // port of fastdns, no show required
            i.putExtra("type_conn", getTypeConnection(requireContext()))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(i)
            } else {
                requireContext().startService(i)
            }


            createOpenVpnCertFastDNS()

            //     btnFastDNSStart!!.text = requireContext().resources.getString(string.payload_stop)

            if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                timer_layout!!.visibility = GONE
            } else {
                timer_layout!!.visibility = VISIBLE
            }
            btnFastDNSStart!!.setImageResource(R.drawable.connected)
            spConnectionMethod!!.isEnabled = false
            open_server_list!!.isEnabled = false
        } else {


            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS, false
            )

            val i = Intent(
                context, DnsService::class.java
            )
            requireContext().stopService(i)

            /** V2RAY Stop */
            // mainActivity.stopVpn()
            /** OPENVPN STOP */
            disconnectOpenVpn()
            timeLeft_val!!.text =
                "00:" + getBestServer_after_calculation(choose_Server_preference)!!.timer_val.toString() + ":00"
            btnFastDNSStart!!.setImageResource(R.drawable.disconnected)
            timer_layout!!.visibility = GONE

            spConnectionMethod!!.isEnabled = true
            open_server_list!!.isEnabled = true


        }
    }


    private fun udpModeConnection() {
        val mainActivity: MainActivity = activity as MainActivity
        if (!DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_UDP_MODE,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_UDP_MODE_DEFAULT
            )
        ) {
            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_UDP_MODE, true
            )

            val serverIP = getConfiguredServer(requireContext()).split(':')[0]


            val i = Intent(
                context, DnsService::class.java
            )
            i.putExtra("host", serverIP)
            i.putExtra("extra", "53") // port of udp mode, no show required
            i.putExtra("type_conn", getTypeConnection(requireContext()))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(i)
            } else {
                requireContext().startService(i)
            }

            createOpenVpnCert()


            btnFastDNSStart!!.setImageResource(R.drawable.connected)
            if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                timer_layout!!.visibility = GONE
            } else {
                timer_layout!!.visibility = VISIBLE
            }
            spConnectionMethod!!.isEnabled = false
            open_server_list!!.isEnabled = false


        } else {
            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_UDP_MODE, false
            )

            val i = Intent(
                context, DnsService::class.java
            )
            requireContext().stopService(i)
            mainActivity.stopVpn()
            //disconnectOpenVpn()

            //     btnFastDNSStart!!.text = requireContext().resources.getString(string.payload_start)

            btnFastDNSStart!!.setImageResource(R.drawable.disconnected)

            timer_layout!!.visibility = GONE
            spConnectionMethod!!.isEnabled = true
            open_server_list!!.isEnabled = true


        }
    }

    private fun importClipboard(): Boolean {
        try {
            var clipboard: String? = null
            if (v2rayportudp.equals("true")) {
                // Procolo v1
                // de este requiere ambos IP y Dominio TLS de los que mandara el api ip reemplazaria esto, (5.161.180.206) y dominio TLS seria este (dnstt-test2.dnstunnel-udp.tk)
                //
                // vless://3a3c3a0e-9e81-49e6-949d-090d83a1f1fb@5.161.180.206:443?security=tls&encryption=none&headerType=none&type=quic&quicSecurity=none&key=&sni=dnstt-test2.dnstunnel-udp.tk#vless-quic+udp
                clipboard = getBestServer_after_calculation(choose_Server_preference)!!.v2ray_udp
            } else {
                // Procolo v1
                // TCP el perfil estara cargado simplemente cambiara el dominio TLS de esta configuracion en este texto solo seria (dnstt-test.dnstunnel-udp.tk)
                //vless://3a3c3a0e-9e81-49e6-949d-090d83a1f1fb@dnstt-test.dnstunnel-udp.tk:443?type=ws&encryption=none&security=tls&path=%2fmcjorsh#vless-ws-tls
                clipboard = getBestServer_after_calculation(choose_Server_preference)!!.v2ray_tcp
            }

            importBatchConfig(clipboard)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun importClipboardHard(): Boolean {
        try {
            var clipboard: String? = null
            clipboard = getBestServer_after_calculation(choose_Server_preference)!!.v2ray_tcp


            // Procolo v1
            // Este simplemente requiere dominio TLS es lo unico que cambiara y recuerda que esto cambiara siempre  igual para los otros tiene que cambiar segun el servidor seleccionado dnstt-test2.dnstunnel-udp.tk esto reemplazara este
            // sigue siendo el mismo de los de arriba DOMINIOS TLS   para el  Dominio de DNSTT va en otro lado, pero en ese no hay problema no hay que ajustarle
            //
            // TUNNEL PARA PAYLOAD Y DNSTT
            importBatchConfig("vless://3a3c3a0e-9e81-49e6-949d-090d83a1f1fb@127.0.0.1:2323?mode=gun&security=tls&encryption=none&type=grpc&serviceName=403CYRL&sni=tls.dnstunnel-udp.tk#vless-gRPC")
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun loadAdmobBanner() {
        if (!Constant.pay_done) {
            if (Constant.banner_switch) {
                adView?.setAdSize(AdSize.BANNER)
                layoutad?.setVisibility(VISIBLE)
                layoutad?.addView(adView)
                adView?.setAdUnitId(Constant.admob_banner)
                val adRequest = AdRequest.Builder().build()
                adView?.loadAd(adRequest)
            }
        }
    }

    private fun importBatchConfig(server: String?) {
        val subid = "v2ray_ic_config"
        var count = AngConfigManager.importBatchConfig(server, subid, subid.isNullOrEmpty())
        if (count <= 0) {
            count = AngConfigManager.importBatchConfig(
                com.v2ray.ang.util.Utils.decode(server!!), subid, subid.isNullOrEmpty()
            )
        }
        if (count > 0) {
            /*    TastyToast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.toast_success),
                    Toast.LENGTH_LONG,
                    TastyToast.SUCCESS
                ).show()*/
            btnStartV2RayCC!!.isEnabled = true
        } else {
            TastyToast.makeText(
                requireContext(),
                requireContext().getString(R.string.toast_failure),
                Toast.LENGTH_LONG,
                TastyToast.ERROR
            ).show()
            btnStartV2RayCC!!.isEnabled = true
        }

        val serverConfig = MmkvManager.decodeServerConfig("null")
    }

    private fun readConn(context: Context): String {
        val file = "conn_v2ray_conf.txt"

        val fileCheck = File(file)
        if (!fileCheck.exists()) {
            return ""
        }

        var fileInputStream: FileInputStream? = null
        fileInputStream = context.openFileInput(file)
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = null
        while (run {
                text = bufferedReader.readLine()
                text
            } != null) {
            stringBuilder.append(text)
        }

        return stringBuilder.toString().trim()
    }

    private fun writeConn() {

        val file = "conn_json.txt"
        val data: String = getTypeConnection(requireContext())!!

        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = requireContext().openFileOutput(file, Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
        } catch (e: Exception) {

        }

    }

    public fun stopPayloadProcess() {
        e(
            LogUtil.DIAM_PROXY_TAG,
            "$TAG: " + DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_PAYLOAD,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_PAYLOAD_DEFAULT
            ).toString()
        )
        if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_PAYLOAD,
                DmPrefsKeys.SHARED_PREFERENCES_STARTED_PAYLOAD_DEFAULT
            )
        ) {
            btnHttpRequestStart!!.callOnClick()
        }
    }

    private fun onServerPortSelectedListener(parent: AdapterView<*>?, position: Int) {
        val selectedServerPort: ServerModelPort =
            parent!!.getItemAtPosition(position) as ServerModelPort

        if (context != null) {
            DmSharedPreferences.getInstance(requireContext())!!.putLong(
                    DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT, selectedServerPort.port
                )
        }
    }

    private fun onServerSelectedListener(
        adapterView: AdapterView<*>,
        i: Int,
        isPayload: Boolean,
        isFastDns: Boolean,
        isv2ray: Boolean,
        isUdpMode: Boolean
    ) {
        var selectedServer = adapterView.getItemAtPosition(i) as api_response




        api_model_updated = api_response()
        api_model_updated!!.server_id = 10000
        api_model_updated!!.hostName =
            MyApplication.appContext.getString(string.automatic_server_name)
        api_model_updated!!.ip = ""
        api_model_updated!!.port = 0
        api_model_updated!!.drawable = R.drawable.germany.toString()
        api_model_updated!!.city = MyApplication.appContext.getString(
            string.automatic_server_desc
        )
        api_model_updated!!.publickey = ""
        serverList!!.add(api_model_updated!!)


        selectedServer = api_model_updated!!


        if (context != null) {
            if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                    DmPrefsKeys.SHARED_PREFERENCES_VPN_BY_IP,
                    DmPrefsKeys.SHARED_PREFERENCES_VPN_BY_IP_DEFAULT
                )
            ) {
                DmSharedPreferences.getInstance(requireContext())!!.putString(
                        DmPrefsKeys.SHARED_PREFERENCES_SERVER, selectedServer.ip
                    )
            } else {
                DmSharedPreferences.getInstance(requireContext())!!.putString(
                        DmPrefsKeys.SHARED_PREFERENCES_SERVER, selectedServer.ip
                    )
            }
        }

        selectedport = ArrayList()
        selectedport!!.add(443)

        val adapterServerPorts = ServerPortAdapter(requireContext(), selectedport!!)

        val server = getConfiguredServer(requireContext(), false).split(":").toTypedArray()
        val portPosition = 443

        when {
            isPayload -> {
                spPayloadServerPort!!.adapter = adapterServerPorts
                spPayloadServerPort!!.setSelection(portPosition)
            }
            isFastDns -> {
                // No hacer nada porque lleva un unico puerto 53
            }
            isv2ray -> {
                // No hacer nada porque lleva un unico puerto 53
            }
            isUdpMode -> {
                // No hacer nada porque lleva un unico puerto 80
            }
            else -> {
                spServerPort!!.adapter = adapterServerPorts
                spServerPort!!.setSelection(portPosition)
            }
        }
    }

    private fun setCustomDnsConnetion(context: Context, value: Boolean) {
        DmSharedPreferences.getInstance(context)!!.putBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_DNS_CONNECTION, value
        )
    }

    private fun getDnsConnection(context: Context): Boolean {
        return DmSharedPreferences.getInstance(context)!!.getBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_DNS_CONNECTION,
            DmPrefsKeys.SHARED_PREFERENCES_DNS_CONNECTION_DEFAULT
        )
    }

    private fun getConfiguredServer(context: Context, withDnsConnection: Boolean = true): String {
        var configuredServer: String = DmSharedPreferences.getInstance(context)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_SERVER, DmPrefsKeys.SHARED_PREFERENCES_SERVER_DEFAULT
        ).toString() + ":" + DmSharedPreferences.getInstance(context)!!.getLong(
            DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT,
            DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT_DEFAULT
        )
        if (withDnsConnection && getDnsConnection(context)) {
            configuredServer = DmSharedPreferences.getInstance(context)!!.getString(
                DmPrefsKeys.SHARED_PREFERENCES_DNS, DmPrefsKeys.SHARED_PREFERENCES_DNS_DEFAULT
            ) + "." + configuredServer
        }
        return "67.205.169.117"
    }

    @Suppress("DUPLICATE_LABEL_IN_WHEN")
    private fun getConfiguredSni(context: Context): String? {
        var customHost = DmSharedPreferences.getInstance(context)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_SNI, DmPrefsKeys.SHARED_PREFERENCES_SNI_DEFAULT
        )
        when (customHost) {
            Constants.DEAFULT_HOST -> customHost = Constants.DEAFULT_HOST_NICKNAME
        }
        return customHost
    }

    private fun getConfiguredDNS(context: Context): String? {
        if (etDns!!.text.isNotEmpty()) {
            DmSharedPreferences.getInstance(context)!!.putString(
                DmPrefsKeys.SHARED_PREFERENCES_DNS, etDns!!.text.toString()
            )
        }
        return DmSharedPreferences.getInstance(context)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_DNS, DmPrefsKeys.SHARED_PREFERENCES_DNS_DEFAULT
        )
    }

    private fun getConfiguredDNSTT(context: Context): String? {
        if (etDns!!.text.isNotEmpty()) {
            DmPrefsMethods.setDnsTTHost(etDtt!!.text.toString())
        }
        return DmPrefsMethods.getDnsTTHost()
    }

    fun onRbClickedDNSTT(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            var type = DmPrefsMethods.DnsTtType.UDP.value

            when (view.getId()) {
                R.id.rbUDP -> if (checked) {
                    type = DmPrefsMethods.DnsTtType.UDP.value
                }
                R.id.rbDOT -> if (checked) {
                    type = DmPrefsMethods.DnsTtType.DOT.value
                }
                R.id.rbDOH -> if (checked) {
                    type = DmPrefsMethods.DnsTtType.DOH.value
                }
            }
            DmPrefsMethods.setConnectionDnsTT(type)
            return
        }
    }

    private fun getConfiguredPayload(context: Context): String? {
        return DmSharedPreferences.getInstance(context)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG,
            DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG_DEFAULT
        )
    }

    private fun getTypeConnection(context: Context): String? {
        return DmSharedPreferences.getInstance(context)!!.getString(
            DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION,
            DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION_DEFAULT
        )
    }

    private fun isImportFile(context: Context): Boolean {
        return DmSharedPreferences.getInstance(context)!!.getBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF,
            DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF_DEFAULT
        )
    }

    private fun isHttpConnection(context: Context): Boolean {
        return getTypeConnection(context) == context.getString(string.http_connection)
    }

    private fun isDnsConnection(context: Context): Boolean {
        return getTypeConnection(context) == context.getString(string.dns_connection)
    }

    private fun isDirectConnection(context: Context): Boolean {
        return getTypeConnection(context) == context.getString(string.direct_connection)
    }

    private fun isFDnsConnection(context: Context): Boolean {
        return getTypeConnection(context) == context.getString(string.fastdns_connection)
    }

    private fun isOpenVpnConnection(context: Context): Boolean {
        val connection = getTypeConnection(context)

        return connection == context.getString(string.open_vpn_connection)
    }

    private fun isUdpModeConnection(context: Context): Boolean {
        val connection = getTypeConnection(context)

        return connection == context.getString(string.UDP_connection)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainActivity.FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { documentUri ->
                requireActivity().contentResolver.takePersistableUriPermission(
                    documentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                val pathData = documentUri.toString()
                if (!pathData.contains(Constants.EXT_FILE)) {
                    TastyToast.makeText(
                        requireContext(),
                        getString(string.file_invalid),
                        Toast.LENGTH_LONG,
                        TastyToast.ERROR
                    ).show()
                    return
                }
                readConfigurationFile(documentUri)
            }
        }
        if (requestCode == IMPORT_FILE_FROM_EXTERNAL_APP && resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                TastyToast.makeText(
                    requireContext(),
                    getString(string.MSG_OPEN_FILE_MANUALLY),
                    TastyToast.LENGTH_LONG,
                    TastyToast.WARNING
                ).show()
            } else {
                val pathData = data!!.data!!.path
                readConfigurationFile(pathData)
            }
        }
        if (requestCode == PAYLOAD_GENERATOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
// activar debug

            etPayload!!.setText(getConfiguredPayload(requireContext()))
            spConnectionMethod!!.setSelection(adapter!!.getPosition(getString(string.payload_connection)))
        }
    }

    fun setFileImportedPath(pathData: String?) {
        fileImportedPath = pathData
    }

    private fun readConfigurationFile(pathData: String?) {
        // Open a specific media item using InputStream
        readConfigurationFile(pathData = pathData, pathURI = null)
    }

    private fun readConfigurationFile(pathURI: Uri?) {
        readConfigurationFile(pathData = null, pathURI = pathURI)
    }

    @SuppressLint("DefaultLocale")
    private fun readConfigurationFile(pathData: String?, pathURI: Uri?) {
        try {
            val fileData = if (pathData != null) {
                Utils.readExternalFile(
                    pathData.replace(
                        "/external_files", Environment.getExternalStorageDirectory().absolutePath
                    ).replace(
                        "/media", Environment.getExternalStorageDirectory().absolutePath
                    )
                ).toString(Charsets.UTF_8)
            } else {
                Utils.readExternalFile(requireContext(), pathURI)!!.toString(Charsets.UTF_8)
            }

            fileImportedPath = null
            val fileExtracted = Utils.decryptValue(fileData)
            val fileExtractedParts = fileExtracted.split(Constants.FILE_SEPARATOR)

            val typeConfig = Utils.decryptValue(fileExtractedParts[0])
            val server = Utils.decryptValue(fileExtractedParts[1])
            val port = Utils.decryptValue(fileExtractedParts[2]).toLong()
            val host = Utils.decryptValue(fileExtractedParts[3])
            val dns = Utils.decryptValue(fileExtractedParts[4])
            val useCustomPing = Utils.decryptValue(fileExtractedParts[5]).toBoolean()
            val ping = Utils.decryptValue(fileExtractedParts[6]).toLong()
            val payload = Utils.decryptValue(fileExtractedParts[7])
            var customMsg = ""
            try {
                customMsg = Utils.decryptValue(fileExtractedParts[8])
            } catch (e: java.lang.Exception) {
            }
            var fileId = ""
            try {
                fileId = Utils.decryptValue(fileExtractedParts[9])
            } catch (e: java.lang.Exception) {
            }
            val fileBlocked = Utils.decryptValue(fileExtractedParts[10]).toBoolean()
            val dnsTTHost = Utils.decryptValue(fileExtractedParts[11])
            val connDnsTTHost = Utils.decryptValue(fileExtractedParts[12])


            val fileIdTmp = DmSharedPreferences.getInstance(requireContext())!!.getString(
                DmPrefsKeys.SHARED_PREFERENCES_FILE_NAME,
                DmPrefsKeys.SHARED_PREFERENCES_FILE_NAME_DEFAULT
            )

            if (fileId.isNotEmpty() && fileId == fileIdTmp) {
                TastyToast.makeText(
                    context, getString(string.file_imported), Toast.LENGTH_LONG, TastyToast.SUCCESS
                ).show()
                return
            }

            DmSharedPreferences.getInstance(requireContext())!!.putString(
                DmPrefsKeys.SHARED_PREFERENCES_FILE_NAME, fileId
            )

            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF, true
            )

            DmSharedPreferences.getInstance(requireContext())!!.putString(
                DmPrefsKeys.SHARED_PREFERENCES_TYPE_CONNECTION, typeConfig
            )

            DmSharedPreferences.getInstance(requireContext())!!.putString(
                DmPrefsKeys.SHARED_PREFERENCES_SERVER, server
            )

            DmSharedPreferences.getInstance(requireContext())!!.putLong(
                DmPrefsKeys.SHARED_PREFERENCES_SERVER_PORT, port
            )

            DmSharedPreferences.getInstance(requireContext())!!.putString(
                DmPrefsKeys.SHARED_PREFERENCES_SNI, host
            )

            DmSharedPreferences.getInstance(requireContext())!!.putString(
                DmPrefsKeys.SHARED_PREFERENCES_DNS, dns
            )

            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_DNS_CONNECTION,
                (typeConfig.toLowerCase().trim() == requireContext().getString(
                    string.dns_connection
                ).toLowerCase().trim())
            )

            DmPrefsMethods.setCustomPing(useCustomPing)

            DmSharedPreferences.getInstance(requireContext())!!.putLong(
                DmPrefsKeys.SHARED_PREFERENCES_PING, ping
            )

            DmSharedPreferences.getInstance(requireContext())!!.putString(
                DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG, payload
            )

            DmSharedPreferences.getInstance(requireContext())!!.putString(
                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG, customMsg
            )

            DmSharedPreferences.getInstance(requireContext())!!.putBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_BLOCK_CONF_SERVER, fileBlocked
            )

            DmPrefsMethods.setConnectionDnsTT(connDnsTTHost)
            DmPrefsMethods.setDnsTTHost(dnsTTHost)

            mListener!!.onFragmentStopInteraction()

            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
            requireActivity().startActivity(requireActivity().intent)
            requireActivity().overridePendingTransition(0, 0)
        } catch (exception: Exception) {
            TastyToast.makeText(
                requireContext(),
                getString(string.file_invalid),
                Toast.LENGTH_LONG,
                TastyToast.ERROR
            ).show()
        }
    }

    private fun blockScreenPerImport() {
        if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF,
                DmPrefsKeys.SHARED_PREFERENCES_IS_IMPORT_CF_DEFAULT
            )
        ) {
            pingContainer!!.visibility = View.GONE
            start_ping!!.visibility = View.GONE
            start_ping!!.isEnabled = false
            seekBarPing!!.visibility = View.GONE
            seekBarPing!!.isEnabled = false

            textErrorSni!!.visibility = View.GONE
            etSNI!!.isEnabled = false

            textErrorDns!!.visibility = View.GONE
            etDns!!.isEnabled = false

            tvPing!!.visibility = View.GONE
            tvPing!!.isEnabled = false

            textErrorPayload!!.visibility = View.GONE
            //  textErrorPayload!!.isEnabled = false

            etDtt!!.visibility = View.GONE
            etDtt!!.isEnabled = false
            rgDNSTT!!.visibility = View.GONE
            rgDNSTT!!.isEnabled = false

            if (DmSharedPreferences.getInstance(requireContext())!!
                    .getBoolean(DmPrefsKeys.SHARED_PREFERENCES_BLOCK_CONF_SERVER, false)
            ) {
                serverPortCont!!.visibility = View.GONE
                serverPortCont!!.isEnabled = false
                spPayloadServerPortCont!!.visibility = View.GONE
                spPayloadServerPortCont!!.isEnabled = false

                serverDNSTT!!.visibility = View.GONE
                serverDNSTT!!.isEnabled = false
                serverFasDNSCont!!.visibility = View.GONE
                serverFasDNSCont!!.isEnabled = false
            }

            val customMsg = DmSharedPreferences.getInstance(requireContext())!!.getString(
                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG,
                DmPrefsKeys.SHARED_PREFERENCES_FE_CUSTOM_MSG_DEFAULT
            )
            if (!customMsg.isNullOrEmpty()) {
                txtEFCustomMsg!!.text = customMsg
                txtEFCustomMsg!!.visibility = View.VISIBLE
            }

            connectionMethodContainer!!.visibility = View.INVISIBLE
            val params: ViewGroup.LayoutParams = connectionMethodContainer!!.layoutParams
            params.width = 0
            params.height = 0
            connectionMethodContainer!!.layoutParams = params
        }
    }

    override fun onDetach() {

        super.onDetach()
        mListener = null
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)
    }

    interface OnFragmentInteractionListener {
        fun onFragmentStartInteraction()
        fun onFragmentStopInteraction()
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {

        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onStart() {
        requireActivity().registerReceiver(
            broadcastReceiver, IntentFilter("com.nicadevelop.nicavpn.countdown_br")
        )
        super.onStart()
    }


    override fun onResume() {
        if (spConnectionMethod != null) {

//            requireActivity().registerReceiver(broadcastReceiver, IntentFilter(Connection_Timer.COUNTDOWN_BR))
            var passStr: String? = null

            if (spConnectionMethod!!.selectedItem == null) {
                spConnectionMethod!!.setSelection(adapter!!.getPosition(getString(string.fastdns_connection)))
                passStr = spConnectionMethod!!.selectedItem.toString()
            } else {
                passStr = spConnectionMethod!!.selectedItem.toString()
            }

            set_initial_value_time()

            if (passStr == "FastDNS") {
                udpandtcp!!.visibility = GONE
                server_country_list!!.text =
                    getBestServer_after_calculation(choose_Server_preference)!!.hostName
                val passStr_server_country: Drawable = getDrawable(
                    getBestServer_after_calculation(choose_Server_preference)!!.drawable,
                    requireActivity()
                )!!
                flag_icon_list!!.setImageDrawable(passStr_server_country)


                txt_fastdns!!.setTextColor(resources.getColor(R.color.colorSelected))
                txt_dnstt!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_openvpn!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_v2ray!!.setTextColor(resources.getColor(R.color.colornonSelected))

                if (DmSharedPreferences.getInstance(requireContext())!!.getBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS,
                        DmPrefsKeys.SHARED_PREFERENCES_STARTED_FAST_DNS_DEFAULT
                    )
                ) {

                    if (getBestServer_after_calculation(choose_Server_preference)!!.timer_val == 0) {
                        timer_layout!!.visibility = GONE
                    } else {
                        timer_layout!!.visibility = VISIBLE
                    }
                    btnFastDNSStart!!.setImageResource(R.drawable.connected)
                    //  btnFastDNSStart!!.text = requireContext().resources.getString(string.payload_stop)
                } else {
                    btnFastDNSStart!!.setImageResource(R.drawable.disconnected)
                    timer_layout!!.visibility = GONE
                    //     btnFastDNSStart!!.text = requireContext().resources.getString(string.payload_start)
                }
            }
            if (passStr == "DNSTT") {
                udpandtcp!!.visibility = GONE
                server_country_list!!.text =
                    getBestServer_after_calculation(choose_Server_preference)!!.hostName
                val passStr_server_dntt: Drawable = getDrawable(
                    getBestServer_after_calculation(choose_Server_preference)!!.drawable,
                    requireActivity()
                )!!
                flag_icon_list!!.setImageDrawable(passStr_server_dntt)


                txt_fastdns!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_dnstt!!.setTextColor(resources.getColor(R.color.colorSelected))
                txt_openvpn!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_v2ray!!.setTextColor(resources.getColor(R.color.colornonSelected))


            }
            if (passStr == "OpenVPN") {
                udpandtcp!!.visibility = VISIBLE
                server_country_list!!.text =
                    getBestServer_after_calculation(choose_Server_preference)!!.hostName
                val passStr_server_openvpn: Drawable = getDrawable(
                    getBestServer_after_calculation(choose_Server_preference)!!.drawable,
                    requireActivity()
                )!!
                flag_icon_list!!.setImageDrawable(passStr_server_openvpn)


                txt_fastdns!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_dnstt!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_openvpn!!.setTextColor(resources.getColor(R.color.colorSelected))
                txt_v2ray!!.setTextColor(resources.getColor(R.color.colornonSelected))


            }
            if (passStr == "V2Ray") {
                udpandtcp!!.visibility = VISIBLE
                if (v2rayportudp.equals("true")) {
                    v2ray_tcp!!.setBackgroundResource(R.drawable.port_bg)
                    v2ray_udp!!.setBackgroundResource(R.drawable.selected_port_bg)
                } else {
                    v2ray_tcp!!.setBackgroundResource(R.drawable.selected_port_bg)
                    v2ray_udp!!.setBackgroundResource(R.drawable.port_bg)
                }
                server_country_list!!.text =
                    getBestServer_after_calculation(choose_Server_preference)!!.hostName
                val passStr_server_v2ray: Drawable = getDrawable(
                    getBestServer_after_calculation(choose_Server_preference)!!.drawable,
                    requireActivity()
                )!!
                flag_icon_list!!.setImageDrawable(passStr_server_v2ray)


                txt_fastdns!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_dnstt!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_openvpn!!.setTextColor(resources.getColor(R.color.colornonSelected))
                txt_v2ray!!.setTextColor(resources.getColor(R.color.colorSelected))


            }

        }




        blockScreenPerImport()

        if (mAdView != null) {
            mAdView!!.resume()
        }

        if (mAdView1 != null) {
            mAdView1!!.resume()
        }

        HTTPtunnelIntentService.checkStatus(activity)

        super.onResume()
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawable(name: String?, context: Context): Drawable? {
        return try {
            val resourceId = context.resources.getIdentifier(name, "drawable", context.packageName)
            ContextCompat.getDrawable(context, resourceId)
        } catch (e: Exception) {
            ContextCompat.getDrawable(context, R.drawable.appicon)
        }
//        return context.resources.getDrawable(resourceId)
    }


    override fun onDestroy() {
        requireActivity().unregisterReceiver(broadcastReceiver)

        if (mAdView != null) {
            mAdView!!.destroy()
        }
        if (mAdView1 != null) {
            mAdView1!!.destroy()
        }

        super.onDestroy()
    }

    companion object {
        private const val TAG = "StartFragment."
        const val PAYLOAD_GENERATOR_REQUEST_CODE = 10000
        const val IMPORT_FILE_FROM_EXTERNAL_APP = 10022

        internal fun newInstance(listener: OnFragmentInteractionListener?): StartFragment {

            val fragment = StartFragment()
            fragment.mListener = listener
            return fragment
        }
    }

    fun getBestServer_after_calculation(sharedPreferences: SharedPreferences?): api_data_model_updated? {
        val responsePojo =
            getPreference("best_server_model", sharedPreferences) as api_data_model_updated?
        return if (responsePojo != null) {
            if (responsePojo.hostName != null) {
                responsePojo
            } else {
                null
            }
        } else null
    }

    fun getPreference(key: String?, global_sharedPreferences: SharedPreferences?): Any? {
        val gson = Gson()
        if (global_sharedPreferences != null) {
            val json_value = global_sharedPreferences.getString(key, null)

            if (json_value != null) {
                if (json_value.isNotEmpty()) {
                    try {
                        return gson.fromJson(json_value, api_data_model_updated::class.java)
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                } else {
                    return null
                }
            }
        }
        return null
    }
}

fun Spinner.avoidDropdownFocus() {
    try {
        val isAppCompat = this is androidx.appcompat.widget.AppCompatSpinner
        val spinnerClass =
            if (isAppCompat) androidx.appcompat.widget.AppCompatSpinner::class.java else Spinner::class.java
        val popupWindowClass =
            if (isAppCompat) androidx.appcompat.widget.ListPopupWindow::class.java else android.widget.ListPopupWindow::class.java

        val listPopup =
            spinnerClass.getDeclaredField("mPopup").apply { isAccessible = true }.get(this)
        if (popupWindowClass.isInstance(listPopup)) {
            val popup = popupWindowClass.getDeclaredField("mPopup").apply { isAccessible = true }
                .get(listPopup)
            if (popup is PopupWindow) {
                popup.isFocusable = false
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


}


