package me.lfasmpao.dnstunnel;

import android.content.Context;
import java.io.File;
import java.util.Vector;
import io.michaelrocks.paranoid.Obfuscate;
import me.lfasmpao.dnstunnel.core.UdpModeProcess;


@Obfuscate
public class UdpMode {

    private Context mContext;
    private UdpModeProcess udpModeProcess = null;
    private String host;
    private String port;
    private String listen_host;
    private String listen_port;

    public boolean isRunning() {
        return udpModeProcess != null;
    }

    /**
     * This initializes UdpModem
     *
     * @param context     Android Context
     * @param listen_host Client Listen Host
     * @param listen_port Client Listen Port
     * @param host        Server Host
     * @param port        Server Port
     */
    public UdpMode(Context context, String host, String port,
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
        udpModeProcess = UdpModeProcess.createTunnel(mContext);

        Vector<String> args = new Vector<>();
        args.add(new File(mContext.getApplicationInfo().nativeLibraryDir,
                "libudptunnel.so").getPath());
        args.add("-keystorelisten1"); //Real ServerListen
        args.add(listen_host + ":" + listen_port);
        args.add("-tun2sockstun"); // Real Tunnel LocalVPN
        args.add(host + ":" + port);

        args.add("-buffer");
        args.add("32768");
        args.add("-tcount");
        args.add("30");
        args.add("-rcount");
        args.add("60");

        if (udpModeProcess != null) {
            String[] ar = args.toArray(new String[args.size()]);
            udpModeProcess.start(ar);
        }
    }

    /**
     * Stop the process
     */
    public void stop() {
        if (udpModeProcess != null) {
            udpModeProcess.destroy();
            udpModeProcess = null;
        }
    }
}
