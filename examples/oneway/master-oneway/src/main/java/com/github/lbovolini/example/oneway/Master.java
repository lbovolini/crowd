package com.github.lbovolini.example.oneway;

import com.github.lbovolini.crowd.node.NodeGroup;

import java.io.IOException;

public class Master {

    public static void main(String[] args) throws IOException {
        NodeGroup<RemoteOneWay> nodeGroup = new NodeGroup(RemoteOneWay.class.getName());
        nodeGroup.forOne(remoteOneWay -> remoteOneWay.say(1));
    }
}
