package com.nicadevelop.nicavpn.http.request.listener

interface CounterListener {
    fun countBytes(uploadBytes: Long, downloadBytes: Long)
}