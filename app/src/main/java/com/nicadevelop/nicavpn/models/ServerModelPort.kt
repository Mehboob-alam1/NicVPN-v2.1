package com.nicadevelop.nicavpn.models


class ServerModelPort(
    var id: Int,
    var port: Long
) {

    override fun toString(): String {
        return "ServerModel{" +
                "id=" + id +
                ", port=" + port.toString() +
                '}'
    }
}