package com.github.lbovolini.example.nativeLib;

import com.github.lbovolini.crowd.node.NodeGroup;

import java.io.IOException;

public class Master {

    public static void main(String[] args) throws IOException {

        NodeGroup<RemoteNative> nodeGroup = new NodeGroup<>(Native.class.getName());
        nodeGroup.forOne(remoteNative -> remoteNative.say("World"));
    }

}
