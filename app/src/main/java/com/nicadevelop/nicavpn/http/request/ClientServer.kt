package com.nicadevelop.nicavpn.http.request

import com.nicadevelop.nicavpn.Constants
import com.nicadevelop.nicavpn.MyApplication
import com.nicadevelop.nicavpn.LogFragment
import com.nicadevelop.nicavpn.R
import com.nicadevelop.nicavpn.http.request.listener.ByteCounter
import com.nicadevelop.nicavpn.tools.LogUtil
import io.michaelrocks.paranoid.Obfuscate
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@Obfuscate
open class ClientServer(
    serverAddr: String, serverPort: Int, payloadConfig: String
) : Runnable {
    @Suppress("JoinDeclarationAndAssignment")
    private val listening: InetSocketAddress
    private val target: String
    private var server: InetSocketAddress? = null
    private val serverAddr: String
    private val serverPort: Int
    private var payloadConfig: String? = null
    private var ss: ServerSocket? = null
    private val connections: MutableList<Connection> = LinkedList<Connection>()
    private val connectionsLock: Lock = ReentrantLock()
    private var isStopped = false
    private var counter: ByteCounter? = null
    private var config: ConnectionConfig

    fun start() {
        Thread(this).start()
    }

    open fun setByteCounter(counter: ByteCounter?) {
        this.counter = counter
    }

    fun close() {
        if (!isStopped) {
            //handleLog("closed")
            LogUtil.i(LogUtil.DIAM_PROXY_TAG + "V2RAY", "ClientServer.closed")
            LogFragment.addLog(MyApplication.appContext.getString(R.string.close))//CLOSE
            isStopped = true
            if (counter != null) {
                counter!!.stop()
            }
            if (ss != null) {
                try {
                    ss!!.close()
                } catch (e: IOException) {
                }
            }
            var connections: List<Connection>
            synchronized(
                connectionsLock
            ) { connections = LinkedList(this.connections) }
            for (connection in connections) connection.close()
        }
    }

    override fun run() {
        try {
            LogFragment.addLog(MyApplication.appContext.getString(R.string.listeningon))//listening on
            server = InetSocketAddress(serverAddr, serverPort)
            ss = ServerSocket()
            ss!!.bind(listening)
            while (true) {
                val socket = ss!!.accept()
                acceptConnection(socket)
            }
        } catch (e: IOException) {
            LogFragment.addLog(MyApplication.appContext.getString(R.string.error) + "\r")//ERROR
        } finally {
            close()
        }
    }

    private fun acceptConnection(socket: Socket) {
        val conn = object : Connection(socket, server!!, target, config, payloadConfig!!) {
            override fun onLog(log: String?) {
                this@ClientServer.onLog(log)
            }

            override fun onClosed() {
                connectionsLock.lock()
                try {
                    connections.remove(this)
                } finally {
                    connectionsLock.unlock()
                }
            }
        }
        if (counter != null) conn.setByteCounter(counter)
        synchronized(connectionsLock) { connections.add(conn) }
        conn.start()
        LogFragment.addLog(MyApplication.appContext.getString(R.string.connectionsize) + "\r" + conn.id)//opening connection

    }

    private fun handleLog(log: String) {
        if (!isStopped) onLog("ClientServer: $log")
    }

    open fun onLog(log: String?) {

    }

    init {
        listening = InetSocketAddress(Constants.LISTENING_ADDR, Constants.LISTENING_ADDR_PORT)
        this.target = Constants.LISTENING_TARGET
        config = ConnectionConfig()
        config.hostHeader = serverAddr
        this.serverAddr = serverAddr
        this.serverPort = serverPort
        this.payloadConfig = payloadConfig
    }
}