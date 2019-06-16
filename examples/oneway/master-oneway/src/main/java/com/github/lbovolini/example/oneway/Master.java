package com.github.lbovolini.example.oneway;

import com.github.lbovolini.crowd.node.NodeGroup;

import java.io.IOException;

public class Master {

    public static void main(String[] args) throws IOException {
        NodeGroup<RemoteOneWay> nodeGroup = new NodeGroup(OneWay.class.getName());
        nodeGroup.forOne(oneWay -> oneWay.say(1));
    }
}
