package com.nicadevelop.nicavpn.http.request

import com.nicadevelop.nicavpn.Constants
import com.nicadevelop.nicavpn.MyApplication
import com.nicadevelop.nicavpn.LogFragment
import com.nicadevelop.nicavpn.R
import com.nicadevelop.nicavpn.http.request.listener.ByteCounter
import io.michaelrocks.paranoid.Obfuscate

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

@Obfuscate
open class Connection(
    appSocket: Socket,
    server: InetSocketAddress,
    target: String,
    config: ConnectionConfig,
    payloadConfig: String
) : Runnable {
    private val server: InetSocketAddress
    private val target: String
    private val appSocket: Socket?
    private var appIn: InputStream? = null
    private var appOut: OutputStream? = null
    private var writeSocket: Socket? = null
    private var writeIn: InputStream? = null
    private var writeOut: OutputStream? = null
    private var readSocket: Socket? = null
    private var readIn: InputStream? = null
    private var config: ConnectionConfig
    val id: Int
    private var counter: ByteCounter? = null
    private var threadEndCount = 0
    private val isStopped = AtomicBoolean(false)
    private var payloadConfig: String? = null

    private inner class ThreadAppRead : Runnable {
        fun start() {
            Thread(this).start()
        }

        override fun run() {
            try {
                val buffer = ByteArray(Client.TAM_RECEIVE_BUFFER_APP)
                while (true) {
                    val len = appIn!!.read(buffer)
                    if (len != -1) {
                        if (counter != null) {
                            counter!!.addUploadBytes(len.toLong())
                        }
                        writeOut!!.write(buffer, 0, len)
                    } else {
                        writeSocket!!.shutdownOutput()
                        return
                    }
                }
            } catch (e: IOException) {
                LogFragment.addLog(MyApplication.appContext.getString(R.string.HTTPHeaderERROR) + "\r" + e.message)   //thread app read erro

                close()
            } finally {
                finallyClose()
            }
        }
    }

    private inner class ThreadRead : Runnable {
        fun start() {
            Thread(this).start()
        }

        override fun run() {
            try {
                val buffer = ByteArray(Client.TAM_BUFFER_SERVER_RESPONSE_CONTENT)
                while (true) {
                    val len = readIn!!.read(buffer)
                    if (len != -1) {
                        if (counter != null) {
                            counter!!.addDownloadBytes(len.toLong())
                        }
                        appOut!!.write(buffer, 0, len)
                    } else {
                        appSocket!!.shutdownOutput()
                        return
                    }
                }
            } catch (e: IOException) {
                //handleLog("HTTPHeader read error. " + e.message)// thread read error
                LogFragment.addLog(MyApplication.appContext.getString(R.string.HTTPHeaderreaderror))// thread read error

                close()
            } finally {
                finallyClose()
            }
        }
    }

    fun setByteCounter(counter: ByteCounter?) {
        this.counter = counter
    }

    fun start() {
        Thread(this).start()
    }

    fun close() {
        LogFragment.addLog(MyApplication.appContext.getString(R.string.closeD))///CLOSED

        if (!isStopped.getAndSet(true)) {
            if (writeSocket != null) {
                try {
                    writeSocket!!.close()
                } catch (e: IOException) {
                }
            }
            if (readSocket != null) {
                try {
                    readSocket!!.close()
                } catch (e2: IOException) {
                }
            }
            if (appSocket != null) {
                try {
                    appSocket.close()
                } catch (e3: IOException) {
                }
            }
            onClosed()
        }
    }

    @Synchronized
    private fun finallyClose() {
        threadEndCount++
        if (threadEndCount == 2) {
            close()
        }
    }

    override fun run() {
        try {
            val configs = payloadConfig!!.split("\\[split]").toTypedArray()
            for (config in configs) {
                if (establishConnection(config)) {
                    appIn = appSocket!!.getInputStream()
                    appOut = appSocket.getOutputStream()
                    sendDataRequest(config, writeOut)
                    ThreadAppRead().start()
                    ThreadRead().start()
                    try {
                        Thread.sleep(7000.toLong())
                    } catch (e: Exception) {
                    }
                }
            }
        } catch (e: IOException) {
            //handleLog("error. " + e.message)
            LogFragment.addLog(MyApplication.appContext.getString(R.string.errorC) + "\r" + e.message)
            close()
        }
    }

    @Throws(IOException::class)
    private fun establishConnection(connConfig: String): Boolean {
        writeSocket = Socket()
        writeSocket!!.connect(server)
        writeIn = writeSocket!!.getInputStream()
        writeOut = writeSocket!!.getOutputStream()
        //handleLog("Configuration sent")//sending connection create
        //LogFragment.addLog(applicationContext.getString(R.string.IntentServiceRUN))
        LogFragment.addLog(MyApplication.appContext.getString(R.string.Configurationsent))//sending connection create
/*        LogFragment.addLog("Configuration sent")*/
        sendConnectionCreate(connConfig, writeOut)
        var response = ServerResponse(writeIn)
        if (!response.read()) {
            throw IOException("server sends a invalid response!")
        }
        val id: String = response.id.toString()
        if (!response.statusMsg.equals(Constants.MSG_CONNECTION_CREATED)) {
            throw IOException("connection couldn't be created: " + response.statusMsg)
        }
        //handleLog("pingx created Size (KBps): - $id") //connection created
        //LogFragment.addLog("pingx created Size (KBps): $id") //connection created
        LogFragment.addLog(MyApplication.appContext.getString(R.string.PINGenerator) + "\r" + id)

        readSocket = Socket()
        readSocket!!.connect(server)
        readIn = readSocket!!.getInputStream()
        //handleLog("complete send settings")//sending connection complete
        //LogFragment.addLog("complete send settings")//sending connection complete
        LogFragment.addLog(MyApplication.appContext.getString(R.string.completesendsettings))//sending connection complete
        sendConnectionComplete(connConfig, readSocket!!.getOutputStream(), id)
        response = ServerResponse(readIn)
        return if (!response.read()) {
            throw IOException("server sends a invalid response!")
        } else if (!response.statusMsg.equals(Constants.MSG_CONNECTION_COMPLETED)) {
            throw IOException("connetion couldn't be completed!")
        } else {
            //handleLog("HTTP/1.1 200 OK Connection Established")//connection completed
            LogFragment.addLog(MyApplication.appContext.getString(R.string.Estaok))//sending connection complete
            true
        }
    }

    @Suppress("NAME_SHADOWING")
    private fun buildPayload(payloadConfig: String, action: Int, id: String): StringBuilder {
        var payloadConfig = payloadConfig
        payloadConfig = payloadConfig.replace("[host_port]", Constants.LISTENING_SERVER)
            .replace("[host]", Constants.LISTENING_ADDR)
            .replace("[port]", "${Constants.LISTENING_ADDR_PORT}")
            .replace("[raw]", "CONNECT [host_port] HTTP/1.1")
            .replace("[netData]", "CONNECT [host_port] [protocol]") //new line
            .replace("[crlf][crlf]", "\r\n").replace("[crlf][crlf][crlf]", "\r\n")
            .replace("[crlf][crlf][crlf][crlf]", "\r\n").replace("[crlf]", "\r\n")
            .replace("[cr]", "\r").replace("[cr][cr]", "\r").replace("[cr][cr][cr]", "\r")
            .replace("[cr][cr][cr][cr]", "\r").replace("[lf]", "\n").replace("[lf][lf]", "\n")
            .replace("[lf][lf][lf]", "\n").replace("[lf][lf][lf][lf]", "\n")
            .replace("[lfcr]", "\n\r").replace("[lfcr][lfcr]", "\n\r")
            .replace("[lfcr][lfcr][lfcr]", "\n\r").replace("[lfcr][lfcr][lfcr][lfcr]", "\n\r")
            .replace("[protocol]", "HTTP/1.0").replace(
                "[ua]",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36"
            ).replace("[method]", "CONNECT")

            //para corregir bugs
            .replace("\\n", "\n").replace("\\r", "\n")

        val builder = StringBuilder()
        builder.append(payloadConfig)
        when (action) {
            1 -> {
                builder.append(Constants.ACTION_HEADER + Constants.ACTION_CREATE + "\r\n")
                if (config.pass != null) {
                    builder.append(Constants.PASS_HEADER + config.pass.toString() + "\r\n")
                }
                builder.append(Constants.TARGET_HEADER + target + "\r\n")
                builder.append(Constants.CONTENT_HEADER + "0\r\n\r\n")
            }
            2 -> {
                builder.append(Constants.ACTION_HEADER + Constants.ACTION_COMPLETE + "\r\n")
                builder.append(Constants.ID_HEADER + id + "\r\n")
                builder.append("Connection: close\r\n\r\n")
            }
            3 -> {
                builder.append(Constants.ACTION_HEADER + Constants.ACTION_DATA + "\r\n")
                builder.append(Constants.CONTENT_HEADER + Int.MAX_VALUE + "\r\n")
                builder.append("Connection: Keep-Alive\r\n\r\n")
            }
        }
        return builder
    }

    @Throws(IOException::class)
    private fun sendConnectionCreate(
        connConfig: String, out: OutputStream?
    ) {
        out!!.write(buildPayload(connConfig, 1, "0").toString().toByteArray())
    }

    @Throws(IOException::class)
    private fun sendConnectionComplete(
        connConfig: String, out: OutputStream, id: String
    ) {
        out.write(buildPayload(connConfig, 2, id).toString().toByteArray())
    }

    @Throws(IOException::class)
    private fun sendDataRequest(
        connConfig: String, out: OutputStream?
    ) {
        out!!.write(buildPayload(connConfig, 3, "0").toString().toByteArray())
    }

    private fun handleLog(log: String) {
        if (!isStopped.get()) {
            onLog("Connection: $log")
            LogFragment.addLog(MyApplication.appContext.getString(R.string.Connectionpack) + "\r" + log)
        }
    }

    open fun onLog(log: String?) {
        println(log)
    }

    open fun onClosed() {}

    init {
        id = appSocket.port
        this.appSocket = appSocket
        this.server = server
        this.target = target
        this.config = config
        this.payloadConfig = payloadConfig
    }
}