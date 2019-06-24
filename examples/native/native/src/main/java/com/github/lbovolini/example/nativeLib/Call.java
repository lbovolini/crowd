package com.github.lbovolini.example.nativeLib;

public class Call implements RemoteNative {
    public void say(String str) {
        new Native().say(str);
        System.out.println(str);
    }
}
