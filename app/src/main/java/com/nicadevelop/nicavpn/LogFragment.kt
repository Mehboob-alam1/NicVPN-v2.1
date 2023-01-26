package com.nicadevelop.nicavpn

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.nicadevelop.nicavpn.logger.Log
import com.nicadevelop.nicavpn.logger.LogWrapper
import com.nicadevelop.nicavpn.logger.MessageOnlyLogFilter
import com.nicadevelop.nicavpn.tools.LogUtil
import com.nicadevelop.nicavpn.tools.Utils
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
class LogFragment : Fragment() {
    private var mLogView: LogView? = null
    private var mScrollView: LinearLayout? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mScrollView = LinearLayout(activity)

        val scrollParams = LayoutParams(-1, -1)
        mScrollView!!.layoutParams = scrollParams
        mLogView = LogView(activity)
        val logParams = LayoutParams(scrollParams)
        logParams.height = LayoutParams.WRAP_CONTENT
        mLogView!!.layoutParams = logParams
        mLogView!!.isClickable = true
        mLogView!!.isFocusable = true

        mScrollView!!.addView(this.mLogView)
        return mScrollView
    }

    fun getLogView(): LogView? {
        return mLogView
    }

    fun clear() {
        LogView.arrayList!!.clear()
        addLog(
            StringBuffer().append("Running on ").append(Build.BRAND).append(" ")
                .append(Build.MODEL).append(" (").append(Build.PRODUCT).append(") ")
                .append(Build.MANUFACTURER).append(", Android API ").append(Build.VERSION.SDK)
                .toString()
        )
        addLog("Application version: " + Utils.vb(requireContext()))
    }

    override fun onStart() {
        super.onStart()
        val logWrapper = LogWrapper()
        Log.logNode = logWrapper
        val msgFilter = MessageOnlyLogFilter()
        logWrapper.next = msgFilter
        msgFilter.next = getLogView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StartFragment.
         */
        internal fun newInstance(): LogFragment {
            return LogFragment()
        }

        fun addLog(str: String?) {
            Log.i(LogUtil.DIAM_PROXY_TAG, str)
        }

        fun addLog(tag: String?, str: String?) {
            Log.i(tag, str)
        }

    }
}

