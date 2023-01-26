@file:Suppress("unused", "SameParameterValue")

package com.nicadevelop.nicavpn.http.request

import com.nicadevelop.nicavpn.Constants
import java.io.IOException
import java.io.InputStream

class ServerResponse {
    private var `in`: InputStream? = null
    var statusCode = 0
        private set
    var statusMsg: String? = null
        private set
    var id: String? = null
        private set
    var isValid = false
        private set

    constructor() {}
    constructor(`in`: InputStream?) {
        this.`in` = `in`
    }

    fun setInputStream(`in`: InputStream?) {
        this.`in` = `in`
    }

    @Throws(IOException::class)
    fun read(): Boolean {
        var linha: String?
        var count = 0
        var ignore = false
        isValid = true
        linha = getLinha(`in`, Client.MAX_LEN_SERVER_RESPONSE_HEAD)
        if (linha == null) return false
        if (!setStatusLine(linha)) return false
        while (linha != "\r\n" && count < Client.MAX_LEN_SERVER_RESPONSE_HEAD) {
            linha = getLinha(`in`, Client.MAX_LEN_SERVER_RESPONSE_HEAD)
            if (linha == null) break
            if (!ignore && linha.startsWith(Constants.ID_HEADER)) {
                ignore = true
                id = getHeaderVal(linha)
                if (id == null) isValid = false
            }
            count += linha.length
        }
        return isValid
    }

    @Throws(IOException::class)
    private fun getLinha(`in`: InputStream?, maxQtdBytes: Int): String? {
        val builder = StringBuilder()
        var b: Int
        var count = 0
        while (`in`!!.read().also { b = it } != -1 && count < maxQtdBytes) {
            count++
            builder.append(b.toChar())
            if (b == '\r'.toInt()) {
                b = `in`.read()
                count++
                if (b == -1) break
                builder.append(b.toChar())
                if (b == '\n'.toInt()) break
            }
        }
        return if (b == -1) null else builder.toString()
    }

    private fun setStatusLine(statusLine: String): Boolean {
        var start = statusLine.indexOf(' ')
        if (start == -1) return false
        start++
        var end = statusLine.indexOf(' ', start)
        if (end == -1) return false
        statusCode = try {
            statusLine.substring(start, end).toInt()
        } catch (e: NumberFormatException) {
            return false
        }
        end++
        start = statusLine.indexOf("\r", end)
        statusMsg = if (start == -1) statusLine.substring(end) else statusLine.substring(end, start)
        return true
    }

    private fun getHeaderVal(header: String): String? {
        var ini = header.indexOf(':')
        if (ini == -1) return null
        ini += 2
        val fim = header.indexOf("\r\n")
        if (fim == -1) header.substring(ini)
        return header.substring(ini, fim)
    }
}