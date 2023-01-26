package com.nicadevelop.nicavpn.http.request.listener

abstract class Caller(private val milliseconds: Long) : Runnable {
    private var running = false
    fun start() {
        Thread(this).start()
    }

    open fun stop() {
        running = false
    }

    abstract fun call()
    override fun run() {
        running = true
        while (running) {
            call()
            try {
                Thread.sleep(milliseconds)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

}
