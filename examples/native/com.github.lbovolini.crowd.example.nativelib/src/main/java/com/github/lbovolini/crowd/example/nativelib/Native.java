package com.github.lbovolini.example.nativeLib;

import java.io.Serializable;

public class Native implements RemoteNative, Serializable {

    static {
        System.loadLibrary("hello");
    }

    public native void say(String str);
}
