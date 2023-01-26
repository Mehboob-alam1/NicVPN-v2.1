package com.nicadevelop.nicavpn.http.request.listener

class ByteCounter(milliseconds: Long) : Caller(milliseconds) {
    private var uploadBytes: Long = 0
    private var downloadBytes: Long = 0
    private var listener: CounterListener? = null
    fun setListener(listener: CounterListener?) {
        this.listener = listener
    }

    @Synchronized
    fun addUploadBytes(count: Long) {
        uploadBytes += count
    }

    @Synchronized
    fun addDownloadBytes(count: Long) {
        downloadBytes += count
    }

    @Synchronized
    fun reset() {
        downloadBytes = 0
        uploadBytes = downloadBytes
    }

    @get:Synchronized
    private val bytes: LongArray
        private get() = longArrayOf(uploadBytes, downloadBytes)

    override fun call() {
        val bytes = bytes
        listener!!.countBytes(bytes[0], bytes[1])
    }
}
