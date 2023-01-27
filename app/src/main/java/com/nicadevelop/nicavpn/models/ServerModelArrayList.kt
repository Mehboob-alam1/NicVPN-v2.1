package com.nicadevelop.nicavpn.models

import com.nicadevelop.nicavpn.Api_Fetch_Service.api_response
import com.nicadevelop.nicavpn.MyApplication
import com.nicadevelop.nicavpn.R
import com.nicadevelop.nicavpn.R.drawable
import io.michaelrocks.paranoid.Obfuscate

@Obfuscate
class ServerModelArrayList {
    val serverList: ArrayList<ServerModel> = arrayListOf(automaticServer())
    val serverDirectList: ArrayList<ServerModel> = arrayListOf(automaticServer())
    val serverPayloadList: ArrayList<ServerModel> = arrayListOf(automaticServer())
    val serverFastDnsList: ArrayList<ServerModel> = arrayListOf(automaticServer())
    val serverDnsTTList: ArrayList<ServerModel> = arrayListOf(automaticServer())
    val serverOpenVpnList: ArrayList<ServerModel> = arrayListOf(automaticServer())

    fun getPositionByServerIp(serverIp: String, serverList: ArrayList<api_response>): Int {
        var position = 0
        if (serverIp.isEmpty()) return position
        for (serverModel in serverList) {
            if (serverModel.ip == serverIp) {
                return position
            }
            position++
        }
        return position
    }

//    fun getPositionByServerIp(serverIp: String, serverList: ArrayList<ServerModel>): Int {
//        var position = 0
//        if (serverIp.isEmpty()) return position
//        for (serverModel in serverList) {
//            if (serverModel.ip == serverIp) {
//                return position
//            }
//            position++
//        }
//        return position
//    }

    fun getPositionByServerDomainName(
        domainName: String, serverList: ArrayList<api_response>
    ): Int {
        var position = 0
        if (domainName.isEmpty()) return position
        for (serverModel in serverList) {
            if (serverModel.ip == domainName) {
                return position
            }
            position++
        }
        return position
    }

//    fun getPositionByServerDomainName(domainName: String, serverList: ArrayList<ServerModel>): Int {
//        var position = 0
//        if (domainName.isEmpty()) return position
//        for (serverModel in serverList) {
//            if (serverModel.domainName == domainName) {
//                return position
//            }
//            position++
//        }
//        return position
//    }

    fun getPortPositionByPort(port: Long, server: ServerModel): Int {
        var position = 0
        if (server.ports.isEmpty()) return position
        for (sPort in server.ports) {
            if (sPort.port == port) {
                return position
            }
            position++
        }
        return position
    }

    fun getServerByIp(serverIp: String, serverList: ArrayList<ServerModel>): ServerModel? {
        if (serverIp.isEmpty()) return null
        for (serverModel in serverList) {
            if (serverModel.ip == serverIp) {
                return serverModel
            }
        }
        return null
    }

    fun getServerByDomainName(
        serverDomainName: String, serverList: ArrayList<ServerModel>
    ): ServerModel? {
        if (serverDomainName.isEmpty()) return null
        for (serverModel in serverList) {
            if (serverModel.domainName == serverDomainName) {
                return serverModel
            }
        }
        return null
    }

    init {

        /**
         * ======================================================================
         * DIRECTO DNS / SSL Server ########
         * ======================================================================
         */
        serverList.add(getServerDnsSSL0())
        serverDirectList.add(getServerDnsSSL0())

        serverList.add(getServerDnsSSL1())
        serverDirectList.add(getServerDnsSSL1())


        /**
         * ======================================================================
         * Payload Server
         * ======================================================================
         */

        serverPayloadList.add(getServerPY0())
        // serverPayloadList.add(getServerPY15())


        /**
         * ======================================================================
         * DNSTT Server ############
         * ======================================================================
         */
        serverDnsTTList.add(getServerDNSTT0())
        /**
         * ======================================================================
         * Fast DNS Server  ############
         * ======================================================================
         */
        serverFastDnsList.add(getServerFastDNS5())
        /**
         * ======================================================================
         * Open Vpn Server   ###########
         * ======================================================================
         */
        serverOpenVpnList.add(getServerFastDNS5())
    }


    /**
     * ======================================================================
     * SERVER / IP  FASTDNS / OPENVPN / DNS / SSL
     * ======================================================================
     */


    //ovpn server
    private fun automaticServer(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(10000, 0))

        return ServerModel(
            10000,
            MyApplication.appContext.getString(R.string.automatic_server_name),
            "",
            "",
            serverPorts,
            drawable.ic_baseline_cached_24,
            MyApplication.appContext.getString(R.string.automatic_server_desc),
            "",
            true
        )
    }

    private fun getServerFastDNS5(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(2, PORT_443))
        return ServerModel(
            8, "SV1", "135.125.232.120", "135.125.232.120", serverPorts, drawable.ic_bug_finder, ""
        )
    }


    private fun getServerDnsSSL0(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_33827))
        serverPorts.add(ServerModelPort(2, PORT_2500))
        serverPorts.add(ServerModelPort(2, PORT_26221))
        serverPorts.add(ServerModelPort(3, PORT_9201))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        return ServerModel(
            1,
            "UK-53",
            "193.31.30.97",
            "slnvwnvo70.tk",
            serverPorts,
            drawable.united_kingdom,
            "London"
        )
    }

    private fun getServerDnsSSL1(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(10, PORT_33827))
        serverPorts.add(ServerModelPort(10, PORT_26221))
        serverPorts.add(ServerModelPort(10, PORT_2500))
        serverPorts.add(ServerModelPort(9, PORT_9201))
        serverPorts.add(ServerModelPort(7, PORT_8799))
        serverPorts.add(ServerModelPort(6, PORT_8388))
        serverPorts.add(ServerModelPort(2, PORT_143))
        serverPorts.add(ServerModelPort(2, PORT_443))
        return ServerModel(
            2,
            "SG",
            "15.235.146.67",
            "dsho285rghbu.buzz",
            serverPorts,
            drawable.singapore,
            "Singapour"
        )
    }

    /**
     * ======================================================================
     * SERVER ITEMS PAYLOAD
     * ======================================================================
     */
    private fun getServerPY0(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_33827))
        serverPorts.add(ServerModelPort(2, PORT_2500))
        serverPorts.add(ServerModelPort(2, PORT_26221))
        serverPorts.add(ServerModelPort(3, PORT_9201))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8799))
        serverPorts.add(ServerModelPort(2, PORT_8388))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(3, PORT_8002))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_4500))
        serverPorts.add(ServerModelPort(2, PORT_3128))
        serverPorts.add(ServerModelPort(5, PORT_1723))
        serverPorts.add(ServerModelPort(8, PORT_1701))
        serverPorts.add(ServerModelPort(3, PORT_1080))
        serverPorts.add(ServerModelPort(2, PORT_443))
        serverPorts.add(ServerModelPort(2, PORT_143))
        serverPorts.add(ServerModelPort(2, PORT_53))
        serverPorts.add(ServerModelPort(5, PORT_25))
        serverPorts.add(ServerModelPort(8, PORT_80))
        return ServerModel(
            1,
            "UK 53",
            "208.87.102.7",
            "kfdt9to54.buzz",
            serverPorts,
            drawable.united_kingdom,
            "London, England"
        )
    }


    /**
     * ======================================================================
     * SERVER ITEMS DNSTT
     * ======================================================================
     */

    private fun getServerDNSTT0(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(1, PORT_53))

        return ServerModel(
            1,
            "SG5",
            "gtrhuefhuioerfhujioegtrft.dsho285rghbu.buzz",
            "gtrhuefhuioerfhujioegtrft.dsho285rghbu.buzz",
            serverPorts,
            drawable.singapore,
            "Singapore",
            "b58bd48393fdf63fc5b7a02eacc0b672421929862119e9f6924351280f6b8562"
        )
    }


    companion object {
        private const val PORT_25: Long = 25
        private const val PORT_53: Long = 53
        private const val PORT_80: Long = 80
        private const val PORT_143: Long = 143
        private const val PORT_443: Long = 443
        private const val PORT_1080: Long = 1080
        private const val PORT_1701: Long = 1701
        private const val PORT_1723: Long = 1723
        private const val PORT_3128: Long = 3128
        private const val PORT_4500: Long = 4500
        private const val PORT_8888: Long = 8888
        private const val PORT_8002: Long = 8002
        private const val PORT_8080: Long = 8080
        private const val PORT_8388: Long = 8388
        private const val PORT_8799: Long = 8799
        private const val PORT_9090: Long = 9090
        private const val PORT_9201: Long = 9201
        private const val PORT_26221: Long = 26221
        private const val PORT_2500: Long = 2500
        private const val PORT_33827: Long = 33827
    }
}