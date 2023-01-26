// IVpnService.aidl
package com.nicadevelop.nicavpn;

// Declare any non-default types here with import statements

interface IVpnService {
    boolean isRunning();
    void stop();
}