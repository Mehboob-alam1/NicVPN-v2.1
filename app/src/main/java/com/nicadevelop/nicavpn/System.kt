package com.nicadevelop.nicavpn

import io.michaelrocks.paranoid.Obfuscate


@Obfuscate
internal object System {
    external fun sendfd(fd: Int, sock: String?): Int
    external fun jniclose(fd: Int)
    fun loadLibrary(s: String) {

    }

    init {
        loadLibrary(Constants.LibUltima)
    }
}
