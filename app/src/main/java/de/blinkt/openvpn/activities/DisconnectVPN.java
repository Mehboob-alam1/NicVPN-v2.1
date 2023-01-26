/*
 * Copyright (c) 2020. Leo Francisco Simpao
 */

package de.blinkt.openvpn.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.nicadevelop.nicavpn.R;

import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.core.IOpenVPNServiceInternal;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;


public class DisconnectVPN extends AppCompatActivity {
    private IOpenVPNServiceInternal mService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            mService = IOpenVPNServiceInternal.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);




        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.disconnectdialog)
                .setTitle(getString(R.string.title_cancel))
                .setMessage("Do you want to disconnect?")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.cancel_connection), (dialogInterface, i) -> {
                    ProfileManager.setConntectedVpnProfileDisconnected(this);
                    if (mService != null) {
                        try {
                            mService.stopVPN(false);
                        } catch (Exception e) {
                            VpnStatus.logException(e);
                        } finally {
                            stopService(new Intent(this, OpenVPNService.class));
                        }
                    }
                    dialogInterface.dismiss();
                    finish();
                })
                .setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                }).create();

        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }


    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e("Tag", "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e("Tag", "Was not able to restart application, PM null");
                }
            } else {
                Log.e("Tag", "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e("Tag", "Was not able to restart application");
        }
    }
}
