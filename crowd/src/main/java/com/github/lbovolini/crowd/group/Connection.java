package com.github.lbovolini.crowd.group;

import java.net.InetSocketAddress;

public class Connection {

    private final Multicast multicast;

    public Connection(Multicast multicast) {
        this.multicast = multicast;
    }

    public void send(String type) {
        multicast.send(type);
    }

    public void sendAll(String type) {
        multicast.sendAll(type);
    }

    public void sendToHost(String type, InetSocketAddress address) {
        multicast.sendToHost(type, address);
    }
}
