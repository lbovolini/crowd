package com.github.lbovolini.crowd.example.nativelib;

public class Call implements RemoteNative {
    public void say(String str) {
        new Native().say(str);
        System.out.println(str);
    }
}
