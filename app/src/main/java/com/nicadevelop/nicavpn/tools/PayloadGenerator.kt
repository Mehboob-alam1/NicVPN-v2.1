@file:Suppress("SameParameterValue")

package com.nicadevelop.nicavpn.tools

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import com.nicadevelop.nicavpn.AppoDealAdManager
import com.nicadevelop.nicavpn.BuildConfig
import com.nicadevelop.nicavpn.R
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmPrefsKeys
import com.nicadevelop.nicavpn.tools.sharedPreferences.DmSharedPreferences

import kotlinx.android.synthetic.main.gen_payload_dialog.view.*
import java.lang.Exception


@SuppressLint("StaticFieldLeak")
object PayloadGenerator {
    private var context: Context? = null
    private var radio1: RadioGroup? = null
    private var checkedRadioButtonId: RadioButton? = null
    private var spinnerRequestMethod: Spinner? = null
    private var spinnerInjectMethod: Spinner? = null
    private var editTextInjectUrl: EditText? = null
    private var spinner2: Spinner? = null

    private var checkBoxFrontQuery: CheckBox? = null
    private var checkBoxBackQuery: CheckBox? = null
    private var checkBoxOnlineHost: CheckBox? = null
    private var checkBoxForwardedFor: CheckBox? = null
    private var checkBoxForwardHost: CheckBox? = null
    private var checkBoxKeepAlive: CheckBox? = null
    private var checkBoxReferer: CheckBox? = null
    private var checkBoxUserAgent: CheckBox? = null
    private var checkBoxRealRequest: CheckBox? = null
    private var checkBoxDualConnect: CheckBox? = null
    private var rotationMethodCheckbox: CheckBox? = null
    private var splitNoDelayCheckbox: CheckBox? = null

    interface DialogClickListener {
        fun onPositiveClickListener(
            customPayload: String
        )
    }

    fun initPayloadGenerator(
        context: Context,
        dialogClickListener: DialogClickListener?
    ) {
        this.context = context
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.gen_payload_dialog, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        builder.setTitle("Payload Generator")
        builder.setCancelable(false)
        val dialog = builder.create()

        createEvents(dialogView)

        @Suppress("ConstantConditionIf")
        if (BuildConfig.IS_USE_ADS) {
            AppoDealAdManager.Instance().showBannerAd(context as Activity, dialogView.adView)
        }
        dialogView.btnCancel.setOnClickListener {
            dialog.dismiss()

            val activity = context as Activity
            AppoDealAdManager.Instance().showTopBanner(activity)
        }

        dialogView.btnOk.setOnClickListener {
            val b: String

            val I: String = checkedRadioButtonId!!.text.toString()
            val stringBuilder = StringBuilder()
            val obj = spinnerInjectMethod!!.selectedItem.toString()
            val obj2 = spinnerRequestMethod!!.selectedItem.toString()
            var replace =
                editTextInjectUrl!!.text.toString().replace("http://", "").replace("https://", "")
            if (rotationMethodCheckbox!!.isChecked) {
                replace = "[rotation=$replace]"
            }
            val stringBuilder2 = StringBuilder()
            if (checkBoxFrontQuery!!.isChecked) {
                stringBuilder2.append("$replace@")
            }
            stringBuilder2.append("[host_port]")
            if (checkBoxBackQuery!!.isChecked) {
                stringBuilder2.append("@$replace")
                stringBuilder2.append("@$replace")
            }
            val stringBuilder3 = stringBuilder2.toString()
            var str = ""
            if (I == "SPLIT") {
                str =
                    if (splitNoDelayCheckbox!!.isChecked) if (obj == "Back Inject") "[crlf][splitNoDelay]" else "[splitNoDelay]" else if (obj == "Back Inject") "[crlf][split]" else "[split]"
            }
            if (obj == "Front Inject") {
                stringBuilder.append("$obj2 http://$replace/ HTTP/1.1[crlf]")
            } else if (obj == "Back Inject") {
                stringBuilder.append("CONNECT $stringBuilder3 HTTP/1.1[crlf]$str$obj2 http://$replace/ [protocol][crlf]")
            } else if (!checkBoxRealRequest!!.isChecked) {
                stringBuilder.append("$obj2 $stringBuilder3 [protocol][crlf]")
            } else if (obj == "Back Inject" || checkBoxFrontQuery!!.isChecked || checkBoxBackQuery!!.isChecked) {
                stringBuilder.append("$obj2 $stringBuilder3 [protocol][crlf]")
            } else {
                stringBuilder.append("[netData][crlf]")
            }
            stringBuilder.append("Host: $replace[crlf]")
            if (checkBoxOnlineHost!!.isChecked) {
                stringBuilder.append("X-Online-Host: $replace[crlf]")
            }
            if (checkBoxForwardHost!!.isChecked) {
                stringBuilder.append("X-Forward-Host: $replace[crlf]")
            }
            if (checkBoxForwardedFor!!.isChecked) {
                stringBuilder.append("X-Forwarded-For: $replace[crlf]")
            }
            if (checkBoxReferer!!.isChecked) {
                stringBuilder.append("Referer: $replace[crlf]")
            }
            if (checkBoxKeepAlive!!.isChecked) {
                stringBuilder.append("Connection: Keep-Alive[crlf]")
            }
            if (checkBoxUserAgent!!.isChecked) {
                b = spinner2!!.selectedItem.toString()
                when (b) {
                    "Firefox" -> {
                        stringBuilder.append("User-Agent: Mozilla/5.0 (Android; Mobile; rv:35.0) Gecko/35.0 Firefox/35.0\r\n")
                    }
                    "Chrome" -> {
                        stringBuilder.append("User-Agent: Mozilla/5.0 (Linux; Android 4.4.2; SAMSUNG-SM-T537A Build/KOT49H) AppleWebKit/537.36 (KHTML like Gecko) Chrome/35.0.1916.141 Safari/537.36[crlf]")
                    }
                    "Opera Mini" -> {
                        stringBuilder.append("User-Agent: Opera/9.80 (J2ME/MIDP; Opera Mini/4.1.15231/127.81; U; ru) Presto/2.12.423 Version/12.16[crlf]")
                    }
                    "Puffin" -> {
                        stringBuilder.append("User-Agent: Mozilla/5.0 (X11; U; Linux x86_64; en-gb) AppleWebKit/534.35 (KHTML, like Gecko) Chrome/11.0.696.65 Safari/534.35 Puffin/2.9174AP[crlf]")
                    }
                    "Safari" -> {
                        stringBuilder.append("User-Agent: Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17[crlf]")
                    }
                    "UCBrowser" -> {
                        stringBuilder.append("User-Agent: Mozilla/5.0 (Linux; U; Android 2.3.3; en-us ; LS670 Build/GRI40) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1/UCBrowser/8.6.1.262/145/355[crlf]")
                    }
                    "Default" -> {
                        stringBuilder.append("User-Agent: [ua][crlf]")
                    }
                }
            }
            if (checkBoxDualConnect!!.isChecked) {
                stringBuilder.append("CONNECT [host_port] [protocol][crlf]")
            }
            stringBuilder.append("[crlf]")
            if (obj == "Front Inject") {
                if (!checkBoxRealRequest!!.isChecked) {
                    stringBuilder.append(str + "CONNECT " + stringBuilder3 + " [protocol][crlf]")
                } else if (checkBoxFrontQuery!!.isChecked || checkBoxBackQuery!!.isChecked) {
                    stringBuilder.append(str + "CONNECT " + stringBuilder3 + " [protocol][crlf]")
                } else {
                    stringBuilder.append("$str[netData][crlf]")
                }
            }

            putSpString(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XHOST,
                editTextInjectUrl!!.text.toString()
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XFRONT_QUERY,
                checkBoxFrontQuery!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XBACK_QUERY,
                checkBoxBackQuery!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XONLINE_HOST,
                checkBoxOnlineHost!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XFORWARDED_FOR,
                checkBoxForwardedFor!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XFORWARD_HOST,
                checkBoxForwardHost!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XKEEP_ALIVE,
                checkBoxKeepAlive!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XREFERER,
                checkBoxReferer!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XUSERAGENT,
                checkBoxUserAgent!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XREAL_REQUEST,
                checkBoxRealRequest!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XDUAL_CONNECT,
                checkBoxDualConnect!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XROTATION,
                rotationMethodCheckbox!!.isChecked
            )
            putSpBoolean(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XSPLIT_NO_DELAY,
                splitNoDelayCheckbox!!.isChecked
            )
            putSpInt(DmPrefsKeys.SHARED_PREFERENCES_PG_XRADIO_GROUP, radio1!!.checkedRadioButtonId)
            putSpInt(
                DmPrefsKeys.SHARED_PREFERENCES_PG_REQUEST_METHOD,
                spinnerRequestMethod!!.selectedItemPosition
            )
            putSpInt(
                DmPrefsKeys.SHARED_PREFERENCES_PG_INJECT_METHOD,
                spinnerInjectMethod!!.selectedItemPosition
            )
            putSpInt(DmPrefsKeys.SHARED_PREFERENCES_PG_USER_AGENT, spinner2!!.selectedItemPosition)

            putSpString(DmPrefsKeys.SHARED_PREFERENCES_PAYLOAD_CONFIG, stringBuilder.toString())

            dialogClickListener?.onPositiveClickListener(stringBuilder.toString())
            dialog.dismiss()

            val activity = context as Activity
            AppoDealAdManager.Instance().showTopBanner(activity)
        }

        dialog.show()
    }

    private fun getSpString(key: String, defaultValue: String): String {
        return DmSharedPreferences.getInstance(context!!)!!.getString(key, defaultValue)!!
    }

    private fun putSpString(key: String, value: String) {
        DmSharedPreferences.getInstance(context!!)!!.putString(key, value)
    }

    private fun getSpBoolean(key: String, defaultValue: Boolean): Boolean {
        return DmSharedPreferences.getInstance(context!!)!!.getBoolean(key, defaultValue)
    }

    private fun putSpBoolean(key: String, value: Boolean) {
        DmSharedPreferences.getInstance(context!!)!!.putBoolean(key, value)
    }

    private fun getSpInt(key: String, defaultValue: Int): Int {
        return DmSharedPreferences.getInstance(context!!)!!.getInt(key, defaultValue)
    }

    private fun putSpInt(key: String, value: Int) {
        DmSharedPreferences.getInstance(context!!)!!.putInt(key, value)
    }

    private fun createEvents(dialogView: View?) {
        editTextInjectUrl = dialogView!!.findViewById(R.id.editTextInjectUrl) as EditText
        editTextInjectUrl!!.setText(
            getSpString(
                DmPrefsKeys.SHARED_PREFERENCES_PG_XHOST,
                DmPrefsKeys.SHARED_PREFERENCES_PG_XHOST_DEFAULT
            )
        )
        spinnerRequestMethod = dialogView.findViewById(R.id.spinnerRequestMethod) as Spinner
        spinnerRequestMethod!!.setSelection(
            getSpInt(
                DmPrefsKeys.SHARED_PREFERENCES_PG_REQUEST_METHOD,
                DmPrefsKeys.SHARED_PREFERENCES_PG_REQUEST_METHOD_DEFAULT
            )
        )
        spinnerInjectMethod = dialogView.findViewById(R.id.spinnerInjectMethod) as Spinner
        spinnerInjectMethod!!.setSelection(
            getSpInt(
                DmPrefsKeys.SHARED_PREFERENCES_PG_INJECT_METHOD,
                DmPrefsKeys.SHARED_PREFERENCES_PG_INJECT_METHOD_DEFAULT
            )
        )
        checkBoxFrontQuery = dialogView.findViewById(R.id.checkBoxFrontQuery) as CheckBox
        checkBoxFrontQuery!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XFRONT_QUERY,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XFRONT_QUERY_DEFAULT
        )
        checkBoxBackQuery = dialogView.findViewById(R.id.checkBoxBackQuery) as CheckBox
        checkBoxBackQuery!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XBACK_QUERY,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XBACK_QUERY_DEFAULT
        )
        checkBoxOnlineHost = dialogView.findViewById(R.id.checkBoxOnlineHost) as CheckBox
        checkBoxOnlineHost!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XONLINE_HOST,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XONLINE_HOST_DEFAULT
        )
        checkBoxForwardedFor = dialogView.findViewById(R.id.checkBoxForwardedFor) as CheckBox
        checkBoxForwardedFor!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XFORWARDED_FOR,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XFORWARDED_FOR_DEFAULT
        )
        checkBoxForwardHost = dialogView.findViewById(R.id.checkBoxForwardHost) as CheckBox
        checkBoxForwardHost!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XFORWARD_HOST,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XFORWARD_HOST_DEFAULT
        )
        checkBoxReferer = dialogView.findViewById(R.id.checkBoxReferer) as CheckBox
        checkBoxReferer!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XREFERER,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XREFERER_DEFAULT
        )
        checkBoxKeepAlive = dialogView.findViewById(R.id.checkBoxKeepAlive) as CheckBox
        checkBoxKeepAlive!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XKEEP_ALIVE,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XKEEP_ALIVE_DEFAULT
        )
        checkBoxUserAgent = dialogView.findViewById(R.id.checkBoxUserAgent) as CheckBox
        checkBoxUserAgent!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XUSERAGENT,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XUSERAGENT_DEFAULT
        )
        spinner2 = dialogView.findViewById(R.id.spinner2) as Spinner
        spinner2!!.setSelection(
            getSpInt(
                DmPrefsKeys.SHARED_PREFERENCES_PG_USER_AGENT,
                DmPrefsKeys.SHARED_PREFERENCES_PG_USER_AGENT_DEFAULT
            )
        )
        checkBoxRealRequest = dialogView.findViewById(R.id.checkBoxRealRequest) as CheckBox
        checkBoxRealRequest!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XREAL_REQUEST,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XREAL_REQUEST_DEFAULT
        )
        checkBoxDualConnect = dialogView.findViewById(R.id.checkBoxDualConnect) as CheckBox
        checkBoxDualConnect!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XDUAL_CONNECT,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XDUAL_CONNECT_DEFAULT
        )

        spinner2!!.isEnabled = false
        checkBoxFrontQuery!!.setOnClickListener { checkBoxBackQuery!!.isChecked = false }
        checkBoxBackQuery!!.setOnClickListener { checkBoxFrontQuery!!.isChecked = false }
        checkBoxUserAgent!!.setOnClickListener {
            spinner2!!.isEnabled = !spinner2!!.isEnabled
        }

        radio1 = dialogView.findViewById(R.id.radio1) as RadioGroup
        checkedRadioButtonId = dialogView.findViewById(radio1!!.checkedRadioButtonId) as RadioButton
        rotationMethodCheckbox = dialogView.findViewById(R.id.rotationMethodCheckbox) as CheckBox
        splitNoDelayCheckbox = dialogView.findViewById(R.id.splitNoDelayCheckbox) as CheckBox

        radio1!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, i ->
            try {
                checkedRadioButtonId = radio1!!.findViewById<View>(i) as RadioButton
                if (radio1!!.indexOfChild(checkedRadioButtonId) == 1) {
                    splitNoDelayCheckbox!!.isEnabled = true
                    splitNoDelayCheckbox!!.isChecked = getSpBoolean(
                        DmPrefsKeys.SHARED_PREFERENCES_PG_XSPLIT_NO_DELAY,
                        DmPrefsKeys.SHARED_PREFERENCES_PG_XSPLIT_NO_DELAY_DEFAULT
                    )
                    return@OnCheckedChangeListener
                }
                splitNoDelayCheckbox!!.isEnabled = false
                splitNoDelayCheckbox!!.isChecked = false
            } catch (e: Exception) {
                //LogUtil.//e(LogUtil.DIAM_PROXY_TAG // activar debug // activar debug, "PayloadGenerator.Error: $e")
            }
        })
        radio1!!.check(getSpInt(DmPrefsKeys.SHARED_PREFERENCES_PG_XRADIO_GROUP, R.id.radioMerger))
        rotationMethodCheckbox!!.setOnCheckedChangeListener { _, z ->
            if (z) {
                editTextInjectUrl!!.hint = "ex. bug1.com;bug2.com"
            } else {
                editTextInjectUrl!!.hint = "ex. bug.com"
            }
        }
        rotationMethodCheckbox!!.isChecked = getSpBoolean(
            DmPrefsKeys.SHARED_PREFERENCES_PG_XROTATION,
            DmPrefsKeys.SHARED_PREFERENCES_PG_XROTATION_DEFAULT
        )
    }
}
