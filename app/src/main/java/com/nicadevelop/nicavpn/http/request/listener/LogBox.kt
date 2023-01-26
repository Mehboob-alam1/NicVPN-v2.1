package com.nicadevelop.nicavpn.http.request.listener

import java.util.*

class LogBox(milliseconds: Long) : Caller(milliseconds) {
    private var listener: LogListener? = null
    private val logList: LinkedList<String>
    private var boxSize = 50
    private var lastCount: Long = -1
    private var logCount: Long = 0
    fun setBoxSize(boxSize: Int) {
        this.boxSize = boxSize
    }

    fun setListener(listener: LogListener?) {
        this.listener = listener
    }

    @Synchronized
    fun clearLog() {
        logList.clear()
    }

    @Synchronized
    fun addLog(log: String) {
        if (logList.size == boxSize) {
            logList.poll()
        }
        logList.add(log)
        logCount++
    }

    override fun stop() {
        super.stop()
        call()
    }

    @Synchronized
    override fun call() {
        if (listener != null) {
            if (lastCount != logCount) {
                listener!!.onLog(toString())
                lastCount = logCount
            }
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        val iterator: Iterator<String> = logList.iterator()
        while (iterator.hasNext()) {
            builder.append(
                """
                    ${iterator.next()}
                    
                    """.trimIndent()
            )
        }
        return builder.toString()
    }

    init {
        logList = LinkedList()
    }
}