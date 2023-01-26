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

    fun getPositionByServerDomainName(domainName: String, serverList: ArrayList<api_response>): Int {
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
        serverDomainName: String,
        serverList: ArrayList<ServerModel>
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

        serverList.add(getServerDnsSSL2())
        serverDirectList.add(getServerDnsSSL2())

        serverList.add(getServerDnsSSL3())
        serverDirectList.add(getServerDnsSSL3())

        serverList.add(getServerDnsSSL4())
        serverDirectList.add(getServerDnsSSL4())

        serverList.add(getServerDnsSSL5())
        serverDirectList.add(getServerDnsSSL5())

        serverList.add(getServerDnsSSL6())
        serverDirectList.add(getServerDnsSSL6())

        serverList.add(getServerDnsSSL7())
        serverDirectList.add(getServerDnsSSL7())

        serverList.add(getServerDnsSSL8())
        serverDirectList.add(getServerDnsSSL8())

        serverList.add(getServerDnsSSL10())
        serverDirectList.add(getServerDnsSSL10())

        serverList.add(getServerDnsSSL11())
        serverDirectList.add(getServerDnsSSL11())

        serverList.add(getServerDnsSSL12())
        serverDirectList.add(getServerDnsSSL12())

        serverList.add(getServerDnsSSL13())
        serverDirectList.add(getServerDnsSSL13())

        //serverList.add(getServerDnsSSL14())
        // serverDirectList.add(getServerDnsSSL14())

        //serverList.add(getServerDnsSSL014())
        //serverDirectList.add(getServerDnsSSL014())


        /**
         * ======================================================================
         * Payload Server
         * ======================================================================
         */

        serverPayloadList.add(getServerPY0())
        serverPayloadList.add(getServerPY1())
        serverPayloadList.add(getServerPY2())
        serverPayloadList.add(getServerPY3())
        serverPayloadList.add(getServerPY03())
        //serverPayloadList.add(getServerPY4())
        serverPayloadList.add(getServerPY5())
        serverPayloadList.add(getServerPY6())
        serverPayloadList.add(getServerPY7())
        serverPayloadList.add(getServerPY8())
        //serverPayloadList.add(getServerPY9())
        // serverPayloadList.add(getServerPY10())
        serverPayloadList.add(getServerPY11())
        serverPayloadList.add(getServerPY12())
        serverPayloadList.add(getServerPY13())
        serverPayloadList.add(getServerPY14())
        //serverPayloadList.add(getServerPY014())
        // serverPayloadList.add(getServerPY015())
        // serverPayloadList.add(getServerPY15())


        /**
         * ======================================================================
         * DNSTT Server ############
         * ======================================================================
         */
        serverDnsTTList.add(getServerDNSTT0())
        serverDnsTTList.add(getServerDNSTT1())
        serverDnsTTList.add(getServerDNSTT2())
        ////  serverDnsTTList.add(getServerDNSTT03())
        serverDnsTTList.add(getServerDNSTT3())
        serverDnsTTList.add(getServerDNSTT4())
        serverDnsTTList.add(getServerDNSTT05())
        //serverDnsTTList.add(getServerDNSTT005())
        // serverDnsTTList.add(getServerDNSTT6())
        //serverDnsTTList.add(getServerDNSTT7())
        serverDnsTTList.add(getServerDNSTT8())
        serverDnsTTList.add(getServerDNSTT9())
        //serverDnsTTList.add(getServerDNSTT10())
        //serverDnsTTList.add(getServerDNSTT11())
        /**
         * ======================================================================
         * Fast DNS Server  ############
         * ======================================================================
         */
        //serverFastDnsList.add(getServerFastDNS1())
        // serverFastDnsList.add(getServerFastDNS2())
        // serverFastDnsList.add(getServerFastDNS3())
        serverFastDnsList.add(getServerFastDNS4())
        serverFastDnsList.add(getServerFastDNS5())
        serverFastDnsList.add(getServerFastDNS6())
        /**
         * ======================================================================
         * Open Vpn Server   ###########
         * ======================================================================
         */
        //serverOpenVpnList.add(getServerFastDNS1())
        //  serverOpenVpnList.add(getServerFastDNS2())
        //  serverOpenVpnList.add(getServerFastDNS3())
        serverOpenVpnList.add(getServerFastDNS4())
        serverOpenVpnList.add(getServerFastDNS5())
        serverOpenVpnList.add(getServerFastDNS6())
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
            8,
            "SV1",
            "135.125.232.120",
            "135.125.232.120",
            serverPorts,
            drawable.ic_bug_finder,
            ""
        )
    }

    private fun getServerFastDNS6(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(2, PORT_443))
        return ServerModel(
            8,
            "SV2",
            "31.192.237.27",
            "31.192.237.27",
            serverPorts,
            drawable.ic_bug_finder,
            ""
        )
    }

    private fun getServerFastDNS4(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(2, PORT_443))
        return ServerModel(
            8,
            "SV3",
            "49.12.76.212",
            "49.12.76.212",
            serverPorts,
            drawable.ic_bug_finder,
            ""
        )
    }
//ovpn server













    private fun getServerDnsSSL0(): ServerModel {
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
        serverPorts.add(ServerModelPort(2, PORT_143))
        serverPorts.add(ServerModelPort(8, PORT_80))
        serverPorts.add(ServerModelPort(2, PORT_53))
        serverPorts.add(ServerModelPort(5, PORT_25))
        serverPorts.add(ServerModelPort(2, PORT_443))
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

    private fun getServerDnsSSL2(): ServerModel {
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
            "51.77.222.212",
            "cxvmndjeoewr9032.buzz",
            serverPorts,
            drawable.france,
            "Gravelines"
        )
    }

    private fun getServerDnsSSL3(): ServerModel {
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
            3,
            "CA",
            "51.161.32.144",
            "fsjdoiiog4iogtrujiongtrvb.buzz",
            serverPorts,
            drawable.canada,
            "Toronto"
        )
    }

    private fun getServerDnsSSL4(): ServerModel {
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
            3,
            "CA2",
            "149.56.47.171",
            "t5x2ccxwas333o43wds.xyz",
            serverPorts,
            drawable.canada,
            "Toronto"
        )
    }

    private fun getServerDnsSSL5(): ServerModel {
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
            3,
            "DE",
            "51.75.73.180",
            "xccxw333o.buzz",
            serverPorts,
            drawable.germany,
            "Frankfurt"
        )
    }

    private fun getServerDnsSSL6(): ServerModel {
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
        serverPorts.add(ServerModelPort(2, PORT_143))
        serverPorts.add(ServerModelPort(8, PORT_80))
        serverPorts.add(ServerModelPort(2, PORT_53))
        serverPorts.add(ServerModelPort(5, PORT_25))
        serverPorts.add(ServerModelPort(2, PORT_443))
        return ServerModel(
            3,
            "DE2-53",
            "135.125.232.120",
            "theproffesor-fuckener.tk",
            serverPorts,
            drawable.germany,
            "Fráncfort"
        )
    }

    private fun getServerDnsSSL7(): ServerModel {
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
        serverPorts.add(ServerModelPort(2, PORT_143))
        serverPorts.add(ServerModelPort(8, PORT_80))
        serverPorts.add(ServerModelPort(2, PORT_53))
        serverPorts.add(ServerModelPort(5, PORT_25))
        serverPorts.add(ServerModelPort(2, PORT_443))
        return ServerModel(
            3,
            "DE3-53",
            "31.192.237.27",
            "theproffesor-fuckener.cf",
            serverPorts,
            drawable.germany,
            "Fráncfort"
        )
    }

    private fun getServerDnsSSL8(): ServerModel {
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
            8,
            "DE",
            "78.46.190.246",
            "xccv100.tk",
            serverPorts,
            drawable.germany,
            "Frank NEW"
        )
    }


    private fun getServerDnsSSL10(): ServerModel {
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
            3,
            "US",
            "45.58.52.97",
            "xccxw333o43wds.xyz",
            serverPorts,
            drawable.united_states,
            "Dallas"
        )
    }

    private fun getServerDnsSSL11(): ServerModel {
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
            3,
            "US2",
            "45.58.56.79",
            "x2ccxwas333o43wds.xyz",
            serverPorts,
            drawable.united_states,
            "Atlanta"
        )
    }

    private fun getServerDnsSSL12(): ServerModel {
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
            3,
            "US3",
            "5.161.133.178",
            "5.161.133.178",
            serverPorts,
            drawable.united_states,
            "Virginia NEW"
        )
    }

    private fun getServerDnsSSL13(): ServerModel {
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
            8,
            "US4",
            "5.180.31.108",
            "5.180.31.108",
            serverPorts,
            drawable.united_states,
            "Miami NEW"
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

    private fun getServerPY1(): ServerModel {
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
            2,
            "UK 53",
            "208.87.98.100",
            "slnvwnvo09.tk",
            serverPorts,
            drawable.united_kingdom,
            "London"
        )
    }

    private fun getServerPY2(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

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

    private fun getServerPY3(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

        return ServerModel(
            3,
            "FR",
            "51.77.222.212",
            "cxvmndjeoewr9032.buzz",
            serverPorts,
            drawable.france,
            "Gravelines"
        )
    }

    private fun getServerPY03(): ServerModel {
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
            3,
            "FR-53",
            "51.254.96.153",
            "9ewop22342.ga",
            serverPorts,
            drawable.france,
            "Estrasburgo"
        )
    }

    private fun getServerPY5(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

        return ServerModel(
            8,
            "CA",
            "51.161.32.144",
            "fsjdoiiog4iogtrujiongtrvb.buzz",
            serverPorts,
            drawable.canada,
            "Estrasburgo"
        )
    }

    private fun getServerPY6(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

        return ServerModel(
            8,
            "DE",
            "78.46.190.246",
            "xccv100.tk",
            serverPorts,
            drawable.germany,
            "Frank NEW"
        )
    }


    private fun getServerPY7(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

        return ServerModel(
            8,
            "CA3",
            "149.56.47.171",
            "t5x2ccxwas333o43wds.xyz",
            serverPorts,
            drawable.canada,
            "Toronto"
        )
    }

    private fun getServerPY8(): ServerModel {
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
            8,
            "DE2-53",
            "51.195.46.23",
            "0i09uy89o.tk",
            serverPorts,
            drawable.germany,
            "Fráncfort"
        )
    }

    private fun getServerPY11(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

        return ServerModel(
            8,
            "US",
            "45.58.52.97",
            "xccxw333o43wds.xyz",
            serverPorts,
            drawable.united_states,
            "Dallas"
        )
    }

    private fun getServerPY12(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

        return ServerModel(
            8,
            "US2",
            "45.58.56.79",
            "x2ccxwas333o43wds.xyz",
            serverPorts,
            drawable.united_states,
            "Atlanta"
        )
    }

    private fun getServerPY13(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

        return ServerModel(
            8,
            "US3",
            "5.161.133.178",
            "5.161.133.178",
            serverPorts,
            drawable.united_states,
            "Virginia new"
        )
    }

    private fun getServerPY14(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(3, PORT_3128))
        serverPorts.add(ServerModelPort(2, PORT_9090))
        serverPorts.add(ServerModelPort(2, PORT_8888))
        serverPorts.add(ServerModelPort(2, PORT_8002))
        serverPorts.add(ServerModelPort(5, PORT_8080))
        serverPorts.add(ServerModelPort(8, PORT_80))

        return ServerModel(
            8,
            "US4",
            "5.180.31.108",
            "5.180.31.108",
            serverPorts,
            drawable.united_states,
            "Miami NEW"
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
    private fun getServerDNSTT1(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(1, PORT_53))

        return ServerModel(
            1,
            "FR",
            "googlepics.cxvmndjeoewr9032.buzz",
            "googlepics.cxvmndjeoewr9032.buzz",
            serverPorts,
            drawable.france,
            "Roubaix",
            "b58bd48393fdf63fc5b7a02eacc0b672421929862119e9f6924351280f6b8562"
        )
    }
    private fun getServerDNSTT2(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(1, PORT_53))

        return ServerModel(
            8,
            "CA",
            "jdwhqwhu.fsjdoiiog4iogtrujiongtrvb.buzz",
            "jdwhqwhu.fsjdoiiog4iogtrujiongtrvb.buzz",
            serverPorts,
            drawable.canada,
            "Toronto",
            "b58bd48393fdf63fc5b7a02eacc0b672421929862119e9f6924351280f6b8562"
        )
    }
    private fun getServerDNSTT3(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(1, PORT_53))

        return ServerModel(
            8,
            "CA",
            "grrefe3.t5x2ccxwas333o43wds.xyz", // sslencryptor-ssv7.tk
            "grrefe3.t5x2ccxwas333o43wds.xyz",  //149.56.47.171
            serverPorts,
            drawable.canada,
            "Toronto",
            "b58bd48393fdf63fc5b7a02eacc0b672421929862119e9f6924351280f6b8562"
        )
    }
    private fun getServerDNSTT4(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(1, PORT_53))

        return ServerModel(
            8,
            "DE",
            "dfvsdsddfsdfdfs.xccxw333o.buzz",
            "dfvsdsddfsdfdfs.xccxw333o.buzz",
            serverPorts,
            drawable.germany,
            "Germany",
            "b58bd48393fdf63fc5b7a02eacc0b672421929862119e9f6924351280f6b8562"
        )
    }
    private fun getServerDNSTT05(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(1, PORT_53))

        return ServerModel(
            8,
            "DE2",
            "ddffd.xccv100.tk",
            "ddffd.xccv100.tk",
            serverPorts,
            drawable.germany,
            "FS new",
            "b58bd48393fdf63fc5b7a02eacc0b672421929862119e9f6924351280f6b8562"
        )
    }
    private fun getServerDNSTT8(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(1, PORT_53))

        return ServerModel(
            8,
            "US",
            "dfsd.vblencryptor-ssv1002.tk",
            "dfsd.vblencryptor-ssv1002.tk",
            serverPorts,
            drawable.united_states,
            "Virginia New",
            "b58bd48393fdf63fc5b7a02eacc0b672421929862119e9f6924351280f6b8562"
        )
    }
    private fun getServerDNSTT9(): ServerModel {
        val serverPorts: ArrayList<ServerModelPort> = ArrayList()
        serverPorts.add(ServerModelPort(1, PORT_53))

        return ServerModel(
            8,
            "US2",
            "tigo.x2ccxwas333o43wds.xyz",
            "tigo.x2ccxwas333o43wds.xyz",
            serverPorts,
            drawable.united_states,
            "Miami",
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