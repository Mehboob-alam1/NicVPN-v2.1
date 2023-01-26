package com.nicadevelop.nicavpn.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.nicadevelop.nicavpn.R
import com.nicadevelop.nicavpn.models.ServerModelPort

import java.util.*


class ServerPortAdapter(mContext: Context, private val mServers: ArrayList<Int>) :
    BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: mInflater.inflate(R.layout.server_adapter, parent, false)
        val ivServer = view.findViewById<ImageView>(R.id.ivServer)
        val tvServer = view.findViewById<TextView>(R.id.tvServer)
        val tvCountry = view.findViewById<TextView>(R.id.tvCountry)
        ivServer.visibility = View.GONE
        tvCountry.visibility = View.GONE
        tvServer.text = mServers[getItemPosition(position)].toString()

        return view
    }

    override fun getItem(position: Int): Any? {
        return mServers[getItemPosition(position)]
    }

    override fun getItemId(position: Int): Long {
        return mServers[getItemPosition(position)].toLong()
    }

    override fun getCount(): Int {
        return mServers.size
    }

    private fun getItemPosition(position: Int): Int {
        var p: Int = position
        if (position == mServers.size) p = position - 1
        return p
    }
}


//package com.nicadevelop.nicavpn.adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseAdapter
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import com.nicadevelop.nicavpn.R
//import com.nicadevelop.nicavpn.models.ServerModelPort
//
//import java.util.*
//
//
//class ServerPortAdapter(mContext: Context, private val mServers: ArrayList<ServerModelPort>) :
//    BaseAdapter() {
//    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//        val view: View = convertView ?: mInflater.inflate(R.layout.server_adapter, parent, false)
//        val ivServer = view.findViewById<ImageView>(R.id.ivServer)
//        val tvServer = view.findViewById<TextView>(R.id.tvServer)
//        val tvCountry = view.findViewById<TextView>(R.id.tvCountry)
//        ivServer.visibility = View.GONE
//        tvCountry.visibility = View.GONE
//        tvServer.text = mServers[getItemPosition(position)].port.toString()
//
//        return view
//    }
//
//    override fun getItem(position: Int): Any? {
//        return mServers[getItemPosition(position)]
//    }
//
//    override fun getItemId(position: Int): Long {
//        return mServers[getItemPosition(position)].id.toLong()
//    }
//
//    override fun getCount(): Int {
//        return mServers.size
//    }
//
//    private fun getItemPosition(position: Int): Int {
//        var p: Int = position
//        if (position == mServers.size) p = position - 1
//        return p
//    }
//}