package me.lfasmpao.dnstunnel;

import android.content.Context;

import java.io.File;
import java.util.Vector;

import io.michaelrocks.paranoid.Obfuscate;
import me.lfasmpao.dnstunnel.core.SlowDnsProcess;

/**
 * The SlowDns implementation uses the dns2tcp
 * https://github.com/alex-sector/dns2tcp
 * https://github.com/lfasmpao/dns2tcp-client-android
 */

@Obfuscate
public class SlowDns {

    public Context context;
    public SlowDnsProcess slowDnsProcess;
    public Vector<String> args;

    /**
     * Check if the Process is running
     * @return boolean
     */
    public boolean isRunning() {
        return slowDnsProcess != null;
    }

    /**
     * This initializes FastDNS
     * @param context Android Context
     * @param listen_port Client Listen Port
     * @param host Server Host
     * @param resource DNS2TCP Resource
     * @param dns_server External DNS Server
     */
    public SlowDns(Context context, String host, String listen_port, String resource, String dns_server) {

        this.context = context;
        this.args = new Vector<>();

        args.add(new File(context.getApplicationInfo().nativeLibraryDir,
                "libslowdns.so").getPath());

        args.add("-r");
        args.add(resource);

        args.add("-l");
        args.add(listen_port);

        args.add("-z");
        args.add(host);

        args.add(dns_server);
    }

    /**
     * Start the FastDNS process
     */
    public void start() {
        slowDnsProcess = SlowDnsProcess.createTunnel(context);
        if (isRunning()) {
            slowDnsProcess.start(
                    args.toArray(new String[args.size()])
            );
        }
    }

    /**
     * Stop the process
     */
    public void stop() {
        if (isRunning()) {
            slowDnsProcess.destroy();
            slowDnsProcess = null;
        }
    }
}
