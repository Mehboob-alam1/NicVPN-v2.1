package me.lfasmpao.dnstunnel;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.Vector;

import io.michaelrocks.paranoid.Obfuscate;
import me.lfasmpao.dnstunnel.core.FastDnsProcess;

/**
 * FastDNS uses DNSSEC for DNS Tunneling
 * It exploits the DNSSec Transactions in order to transfer packets
 * VPN Client -> FastDNS Client (UDP) -> Firewall -> FastDNS Server (UDP) -> VPN Server
 *
 * FastDNS doesn't convert TCP to UDP due to the stability
 * So, you must use an UDP VPN Client
 */

@Obfuscate
public class FastDns {

    private Context mContext;
    private FastDnsProcess fastDnsProcess = null;
    private String host;
    private String port;
    private String listen_host;
    private String listen_port;


    /**
     * Check if the Process is running
     * @return boolean
     */
    public boolean isRunning() {
        return fastDnsProcess != null;
    }

    /**
     * This initializes FastDNS
     * @param context Android Context
     * @param listen_host Client Listen Host
     * @param listen_port Client Listen Port
     * @param host Server Host
     * @param port Server Port
     */
    public FastDns(Context context, String host, String port,
                   String listen_host, String listen_port) {
        this.mContext = context;
        this.host = host;
        this.port = port;
        this.listen_host = listen_host;
        this.listen_port = listen_port;
    }

    /**
     * Start the FastDNS process
     */
    public void start() {
        fastDnsProcess = FastDnsProcess.createTunnel(mContext);

        Vector<String> args = new Vector<>();
        args.add(new File(mContext.getApplicationInfo().nativeLibraryDir,
                "libdns.so").getPath());
        args.add("-listen");
        args.add(listen_host + ":" + listen_port);
        args.add("-tunnel");
        args.add(host + ":" + port);

        Log.d("FastDNS", "start: " + fastDnsProcess);

        if (fastDnsProcess != null) {
            String[] ar = args.toArray(new String[args.size()]);
            fastDnsProcess.start(ar);
        }
    }

    /**
     * Stop the process
     */
    public void stop() {
        if (fastDnsProcess != null) {
            fastDnsProcess.destroy();
            fastDnsProcess = null;
        }
    }
}
