// IRemoteServiceCallback.aidl
package com.prt2121.remoteservice;

// Declare any non-default types here with import statements

oneway interface IRemoteServiceCallback {
    void valueChanged(int value);
}
