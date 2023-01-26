package com.nicadevelop.nicavpn.Api_Fetch_Service;

public class api_data_model_updated {

    private int server_id,ServerStatus,type,port,timer_val;

    private String HostName,city,IP,certificate,username,password,udp,
            tcp,v2ray_udp,v2ray_tcp,publickey,domainname,drawable,ip_dnstt,domainname_dnstt;


    public void setPort(int port) {
        this.port = port;
    }
    public int getPort() {
        return port;
    }

    public void setTimer_val(int timer_val) {
        this.timer_val = timer_val;
    }
    public int getTimer_val() {
        return timer_val;
    }

    public void setServer_id(int server_id) {
        this.server_id = server_id;
    }
    public int getServer_id() {
        return server_id;
    }

    public void setServerStatus(int ServerStatus) {
        this.ServerStatus = ServerStatus;
    }
    public int getServerStatus() {
        return ServerStatus;
    }

    public void set_type(int type) {
        this.type = type;
    }
    public int get_type() {
        return type;
    }

    public void setHostName(String HostName) {
        this.HostName = HostName;
    }
    public String getHostName() {
        return HostName;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
    public String getIP() {
        return IP;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
    public String getCertificate() {
        return certificate;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }
    public String getUdp() {
        return udp;
    }

    public void setTcp(String tcp) {
        this.tcp = tcp;
    }
    public String getTcp() {
        return tcp;
    }

    public void setV2ray_udp(String v2ray_udp) {

        this.v2ray_udp = v2ray_udp;
    }
    public String getV2ray_udp() {

        return v2ray_udp;
    }

    public void setV2ray_tcp(String v2ray_tcp) {
        this.v2ray_tcp = v2ray_tcp;
    }
    public String getV2ray_tcp() {
        return v2ray_tcp;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }
    public String getPublickey() {
        return publickey;
    }



    public void setDrawable(String drawable) {
        this.drawable = drawable;
    }
    public String getDrawable() {
        return drawable;
    }

    public void setIp_dnstt(String ip_dnstt) {
        this.ip_dnstt = ip_dnstt;
    }
    public String getIp_dnstt() {
        return ip_dnstt;
    }


}
