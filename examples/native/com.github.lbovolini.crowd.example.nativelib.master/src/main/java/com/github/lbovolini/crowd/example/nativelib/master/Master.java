package com.github.lbovolini.crowd.example.nativelib;

import com.github.lbovolini.crowd.core.node.Crowd;

import java.io.IOException;

public class Master {

    public static void main(String[] args) throws IOException {

        Crowd<RemoteNative> crowd = new Crowd<>(Call.class.getName());
        crowd.forOne(remoteNative -> remoteNative.say("World"));
    }

}
