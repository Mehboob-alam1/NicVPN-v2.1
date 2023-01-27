package com.nicadevelop.nicavpn.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nicadevelop.nicavpn.Api_Fetch_Service.api_data_model_updated
import com.nicadevelop.nicavpn.Api_Fetch_Service.api_response
import com.nicadevelop.nicavpn.Premium_Feature.Premium_dialog
import com.nicadevelop.nicavpn.R
import java.util.*


class ServerAdapter(
    private val mContext: Context, private val mServers: ArrayList<api_response>
) : RecyclerView.Adapter<ServerAdapter_ViewHolder?>() {

    var choose_Server_preference: SharedPreferences? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerAdapter_ViewHolder {
        val mView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.server_adapter, parent, false)
        return ServerAdapter_ViewHolder(mView)
    }

    @SuppressLint("ObsoleteSdkInt", "SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ServerAdapter_ViewHolder, position: Int) {


        val passStr: String = mServers[position].drawable
        val passStr_2: Drawable = getDrawable(passStr, mContext)!!
        holder.ivServer.setImageResource(R.drawable.germany)
        holder.tvServer.text = mServers[position].hostName
        holder.tvCountry.text = mServers[position].city
        holder.ivServer.setImageDrawable(passStr_2)


        if (mServers[position]._type == 2) {
            holder.ivVip.visibility = VISIBLE
        } else {
            holder.ivVip.visibility = INVISIBLE
        }

        holder.main_server.setOnClickListener {
            choose_Server_preference = mContext.getSharedPreferences("DATA", Context.MODE_PRIVATE)

            if (mServers[position]._type == 2) {
                val dialog = Premium_dialog(mContext as Activity, R.style.AppTheme)
                dialog.show()
            } else {

                //premium servers connection start..!!
                val api_model_serverlist = api_response()
                api_model_serverlist.server_id = mServers[position].server_id
                api_model_serverlist.serverStatus = mServers[position].serverStatus
                api_model_serverlist.hostName = mServers[position].hostName
                api_model_serverlist.city = mServers[position].city
                api_model_serverlist.ip = mServers[position].ip
                api_model_serverlist._type = mServers[position]._type
                api_model_serverlist.timer_val = mServers[position].timer_val
                api_model_serverlist.v2ray_udp = mServers[position].v2ray_udp
                api_model_serverlist.v2ray_tcp = mServers[position].v2ray_tcp
                api_model_serverlist.publickey = mServers[position].publickey
                api_model_serverlist.drawable = mServers[position].drawable
                api_model_serverlist.ip_dnstt = mServers[position].ip_dnstt

                storeValueToPreference(
                    choose_Server_preference, "best_server_model", api_model_serverlist
                )

                if (getBestServer_after_calculation(choose_Server_preference) != null) {
                    if (getBestServer_after_calculation(choose_Server_preference)!!.hostName != null) {
                        (mContext as Activity).finish()
                    }
                }
            }


        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mServers.size
    }

    fun storeValueToPreference(
        sharedPreferences: SharedPreferences?, key: String?, `object`: Any?
    ) {
        if (sharedPreferences != null) {
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(`object`)
            if (sharedPreferences.contains(key)) {
                editor.remove(key).apply()
            }
            editor.putString(key, json)
            editor.apply()
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

    init {
//        modelArrayList = mFlowerList
//        this.picker = picker
//        if (mContext != null) {
//            choose_Server_preference = mContext!!.getSharedPreferences("DATA", Context.MODE_PRIVATE)
//            if (choose_Server_preference != null) {
//                if (choose_Server_preference!!.contains("premium_status")) {
//                    is_premium_server_adapter =
//                        choose_Server_preference!!.getBoolean("premium_status", false)
//                    default_prot = choose_Server_preference!!.getString("default_protocol", null)
//                } else {
//                    is_premium_server_adapter = false
//                }
//            }
//        }
    }
}

class ServerAdapter_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var ivServer: ImageView
    var ivVip: TextView
    var main_server: LinearLayout
    var tvServer: TextView
    var tvCountry: TextView

    init {
        ivServer = itemView.findViewById(R.id.ivServer)
        tvServer = itemView.findViewById(R.id.tvServer)
        main_server = itemView.findViewById(R.id.main_server)
        tvCountry = itemView.findViewById(R.id.tvCountry)
        ivVip = itemView.findViewById(R.id.ivvip)


    }


}
