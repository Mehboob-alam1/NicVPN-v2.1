package com.nicadevelop.nicavpn.http.request

import com.nicadevelop.nicavpn.http.request.listener.ByteCounter
import com.nicadevelop.nicavpn.http.request.listener.LogBox

interface ServiceControl {
    var logBox: LogBox?
    var byteCounter: ByteCounter?
    val clientServer: ClientServer?

    fun setClientServer(
        serverAddr: String?,
        serverPort: Int,
        payloadConfig: String
    )
}