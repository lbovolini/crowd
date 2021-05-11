package com.github.lbovolini.crowd.example.nativelib;

import java.io.Serializable;

public class Native implements RemoteNative, Serializable {

    static {
        System.loadLibrary("crowdexamplehello");
    }

    public native void say(String str);
}
