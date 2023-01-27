@file:Suppress("unused")

package com.nicadevelop.nicavpn

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RelativeLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdRequest.Builder
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
object Constants {
    @kotlin.jvm.JvmField
    var IS_RUN: Boolean= false
    const val EXECUTABLE = "libproxycry.so"
    const val CONFIG = "config.conf"
    const val PSKSECRETS = "psksecrets.txt"
    const val PID = "pid"
    const val DEF_CONFIG = "foreground = yes\n" +
            "client = yes\n" +
            "pid = [PID_KEY]\n" +
            "[VPN]\n" +
            "accept= 2323\n"

    // Configuracion puerto de conexion del servidor
    const val SERVER_LABEL = "connect="


    // Tiempo de configuracion que se muestra el auncio por defecto es 5 minutos
    const val TIME_SHOW_ADMOD_BANNER = 60000L
    const val SNI_LABEL = "sni="

    const val DEAFULT_HOST_NICKNAME = "MOVISTAR-PANAMA"
    const val DEAFULT_HOST = "www.google.com"

    fun createAdBannerView(
        context: Context?,
        bannerKey: String,
        view: RelativeLayout
    ): AdView? {
        if (context != null) {
            val adRequest: AdRequest? = Builder().build()
            val mAdView = AdView(context)
            mAdView.adSize = AdSize.BANNER
            mAdView.adUnitId = bannerKey
            mAdView.loadAd(adRequest)
            view.addView(mAdView)
            return mAdView
        }
        return null
    }


    @SuppressLint("SdCardPath")
    const val DIR = "/data/data/com.nicadevelop.nicavpn/files"

    const val EXT_FILE = ".dpcs"
    const val FILE_SEPARATOR = ";"
    const val AES_FILE_KEY = "MkAllea9yjlqQnPkR5Fwwg=="

    const val ACTION_HEADER = "X-Action: "
    const val TARGET_HEADER = "X-Target: "
    const val ID_HEADER = "X-Id: "
    const val PASS_HEADER = "X-Pass: "
    const val BODY_HEADER = "X-Body: "
    const val CONTENT_HEADER = "Content-Length: "

    const val ACTION_CREATE = "create"
    const val ACTION_COMPLETE = "complete"
    const val ACTION_DATA = "data"

    const val MSG_CONNECTION_CREATED = "Created"
    const val MSG_CONNECTION_COMPLETED = "Completed"

    const val MILLISECONDS_BYTE_COUNTER = 1000L
    const val MILLISECONDS_LOG_REFRESH = 500L
    const val LISTENING_ADDR = "127.0.0.1"
    const val LISTENING_ADDR_PORT = 2323 // target
    const val LISTENING_SERVER = "$LISTENING_ADDR:$LISTENING_ADDR_PORT"
    const val LISTENING_TARGET = "$LISTENING_ADDR:8443" // real servidor


    //Obfucacion Protector strings

    const val LibUltima = "ultimaStub"
}