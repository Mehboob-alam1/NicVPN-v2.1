package com.nicadevelop.nicavpn

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.AttributeSet
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.nicadevelop.nicavpn.logger.LogNode
import com.nicadevelop.nicavpn.tools.Utils
import io.michaelrocks.paranoid.Obfuscate

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Obfuscate
class LogView : ListView, LogNode {
    private var adapter: ArrayAdapter<Spanned>? = null

    internal inner class Takbo(
        private val mLogView: LogView, private val outputBuilder: java.lang.StringBuilder
    ) : Runnable {
        override fun run() {
            mLogView.addLog(outputBuilder.toString())
        }

    }

    constructor(context: Context?) : super(context) {
        arrayList = ArrayList()
        adapter = ArrayAdapter(context!!, R.layout.layout_logtext, arrayList!!)
        setAdapter(adapter)
        adapter!!.notifyDataSetChanged()
        addLog(
            StringBuffer().append("Running on ").append(Build.BRAND).append(" ").append(Build.MODEL)
                .append(" (").append(Build.PRODUCT).append(") ").append(Build.MANUFACTURER)
                .append(", Android API ").append(Build.VERSION.SDK).toString()
        )
        addLog("Application version: " + Utils.vb(context))
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context, attrs
    ) {
    }

    constructor(
        context: Context?, attrs: AttributeSet?, defStyle: Int
    ) : super(context, attrs, defStyle) {
    }

    override fun println(
        priority: Int, tag: String?, msg: String?, tr: Throwable?
    ) {
        var priorityStr: String? = null
        when (priority) {
            Log.VERBOSE -> priorityStr = "VERBOSE"
            Log.DEBUG -> priorityStr = "DEBUG"
            Log.INFO -> priorityStr = "INFO"
            Log.WARN -> priorityStr = "WARN"
            Log.ERROR -> priorityStr = "ERROR"
            Log.ASSERT -> priorityStr = "ASSERT"
            else -> {
            }
        }

        // Handily, the Log class has a facility for converting a stack trace into a usable string.
        var exceptionStr: String? = null
        if (tr != null) {
            exceptionStr = Log.getStackTraceString(tr)
        }

        // Take the priority, tag, message, and exception, and concatenate as necessary
        // into one usable line of text.
        val outputBuilder = java.lang.StringBuilder()
        val delimiter = "\t"
        appendIfNotNull(outputBuilder, priorityStr, delimiter)
        appendIfNotNull(outputBuilder, tag, delimiter)
        appendIfNotNull(outputBuilder, msg, delimiter)
        appendIfNotNull(outputBuilder, exceptionStr, delimiter)

        // In case this was originally called from an AsyncTask or some other off-UI thread,
        // make sure the update occurs within the UI thread.
        (context as Activity).runOnUiThread(Thread(Runnable { // Display the text we just generated within the LogView.
            //new Takbo(LogView.this, outputBuilder);
            addLog(outputBuilder.toString())
        }))
        if (next != null) {
            next!!.println(priority, tag, msg, tr)
        }
    }

    /** Takes a string and adds to it, with a separator, if the bit to be added isn't null. Since
     * the logger takes so many arguments that might be null, this method helps cut out some of the
     * agonizing tedium of writing the same 3 lines over and over.
     * @param source StringBuilder containing the text to append to.
     * @param addStr The String to append
     * @param delimiter The String to separate the source and appended strings. A tab or comma,
     * for instance.
     * @return The fully concatenated String as a StringBuilder
     */
    private fun appendIfNotNull(
        source: java.lang.StringBuilder, addStr: String?, delimiter: String
    ): java.lang.StringBuilder {
        var delimiter: String? = delimiter
        if (addStr != null) {
            if (addStr.length == 0) {
                delimiter = ""
            }
            return source.append(addStr).append(delimiter)
        }
        return source
    }

    // The next LogNode in the chain.
    var next: LogNode? = null

    /** Outputs the string as a new line of log data in the LogView.  */ /*public void appendToLog(String s) {
        append("\n" + s);
    }*/
    fun addLog(str: String) {
        val dateFormat = SimpleDateFormat("[HH:mm]", Locale.getDefault())
        if (str.contains("\n")) {
            val split = str.split("\n").toTypedArray()
            for (str2 in split) {
                arrayList!!.add(
                    Html.fromHtml(
                        StringBuffer().append(dateFormat.format(Date())).append(" ").append(str2)
                            .toString()
                    )
                )
                setSelection(count - 1)
                adapter!!.notifyDataSetChanged()
            }
            return
        }
        arrayList!!.add(
            Html.fromHtml(
                StringBuffer().append(dateFormat.format(Date())).append(" ").append(str).toString()
            )
        )
        adapter!!.notifyDataSetChanged()
        setSelection(count - 1)
    }

    companion object {
        var arrayList: ArrayList<Spanned>? = null
    }
}
