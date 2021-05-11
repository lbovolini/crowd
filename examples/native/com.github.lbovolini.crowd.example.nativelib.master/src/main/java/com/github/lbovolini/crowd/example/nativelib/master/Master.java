package com.github.lbovolini.crowd.example.nativelib.master;

import com.github.lbovolini.crowd.example.nativelib.Call;
import com.github.lbovolini.crowd.example.nativelib.RemoteNative;

import com.github.lbovolini.crowd.server.Crowd;

import java.io.IOException;

public class Master {

    public static void main(String[] args) throws IOException {

        Crowd<RemoteNative> crowd = new Crowd<>(Call.class.getName());
        crowd.forOne(remoteNative -> remoteNative.say("World"));
    }

}
