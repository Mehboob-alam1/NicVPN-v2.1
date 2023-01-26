package de.blinkt.openvpn;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ProfileManager;


public class ClientManager {
    private static String TAG = ClientManager.class.getName();

    public static void startVPN(Context context, VpnProfile vp) {
        Intent intent = new Intent(context.getApplicationContext(), LaunchVPN.class);
        try {
            intent.putExtra(LaunchVPN.EXTRA_KEY, vp.getUUID().toString());
            intent.setAction(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (NullPointerException e) {
            Toast.makeText(context, "Error in OpenVPN configuration\n" +
                    "Please check the OpenVPN configuration settings", Toast.LENGTH_LONG).show();
        }
    }

    public static VpnProfile generateOvpn(Context context, String config, String name) {
        ConfigParser cp = new ConfigParser();
        try {
            byte[] openvpn_conf = config.getBytes(); // generate ovpn file
            InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(openvpn_conf));
            cp.parseConfig(isr);
            VpnProfile vp = cp.convertProfile();
            vp.mName = name;
            ProfileManager.setTemporaryProfile(context, vp);
            return vp;
        } catch (ConfigParser.ConfigParseError | IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
