package me.lfasmpao.dnstunnel;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;
import io.michaelrocks.paranoid.Obfuscate;
import me.lfasmpao.dnstunnel.core.DnsttProcess;


@Obfuscate
public class Dnstt {

    private Context mContext;
    private DnsttProcess dnsttProcess = null;
    private String host;
    private String listen_host;
    private String listen_port;
    private Resolver resolver;
    private String name_server;


    /**
     * Check if the Process is running
     * @return boolean
     */
    public boolean isRunning() {
        return dnsttProcess != null;
    }


    public enum Resolver {
        DOT, DOH, UDP
    }

    /**
     * This initializes DnsTT
     * @param context Android Context
     * @param resolver Type of Resolver
     * @param server_pub_key Server Public Key
     * @param host Server Host
     * @param name_server Server Nameserver
     * @param listen_host Client Listen Host
     * @param listen_port Client Listen Port
     */
    public Dnstt(Context context, Resolver resolver, String host,
                   String server_pub_key, String name_server,
                   String listen_host, String listen_port) {

        this.mContext = context;
        this.host = host;
        this.name_server = name_server;
        this.listen_host = listen_host;
        this.listen_port = listen_port;
        this.resolver = resolver;

        // write to server.pub
        PrintWriter fileWriter = FileUtility.printToFile(
                new File(context.getFilesDir().getAbsolutePath() + "/server.pub"));
        assert fileWriter != null;
        fileWriter.print(server_pub_key);
        fileWriter.close();

    }

    /**
     * Start the FastDNS process
     */
    public void start() {
        dnsttProcess = DnsttProcess.createTunnel(mContext);
        FileUtility fileUtility = new FileUtility(mContext);

        Vector<String> args = new Vector<>();
        String path = new File(mContext.getApplicationInfo().nativeLibraryDir,
                "libdnstt.so").getPath();

        args.add(path);

        //Log.d("DNSTT", "start: " + path);
        switch (resolver) {
            case DOT:
                args.add("-dot");
                break;
            case DOH:
                args.add("-doh");
                break;
            case UDP:
                args.add("-udp");
                break;
        }
        args.add(host);

        args.add("-pubkey-file");
        args.add(fileUtility.getFilePath("server.pub"));

        args.add(name_server);

        args.add(listen_host + ":" + listen_port);

        if (dnsttProcess != null) {
            String[] ar = args.toArray(new String[args.size()]);
            Log.d("DNSTT", "start: " + args.toString());
            dnsttProcess.start(ar);
        }
    }

    /**
     * Stop the process
     */
    public void stop() {
        if (dnsttProcess != null) {
            dnsttProcess.destroy();
            dnsttProcess = null;
        }
    }
}
