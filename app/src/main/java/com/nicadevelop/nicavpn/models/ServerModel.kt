package com.nicadevelop.nicavpn.models

import androidx.annotation.DrawableRes


class ServerModel(
    var id: Int,
    var name: String,
    var ip: String,
    var domainName: String,
    var ports: ArrayList<ServerModelPort>,
    @field:DrawableRes @param:DrawableRes
    var icon: Int,
    var country: String,
    var publicKey: String = "",
    var isAutomatic: Boolean = false
) {

    override fun toString(): String {
        return "ServerModel{" +
                "id=$id" +
                ", name=$name" +
                ", ip=$ip" +
                ", domainName=$domainName" +
                ", port=$ports" +
                ", icon=$icon" +
                ", country=$country" +
                ", publicKey=$publicKey" +
                ", isAutomatic=$isAutomatic" +
                "}"
    }

}