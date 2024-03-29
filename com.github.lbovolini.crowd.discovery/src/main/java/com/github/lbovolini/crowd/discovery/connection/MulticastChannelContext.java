package com.github.lbovolini.crowd.discovery.connection;

import com.github.lbovolini.crowd.discovery.message.ResponseFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

public class MulticastChannelContext {

    private final DatagramChannel channel;
    private final Selector selector;
    private final NetworkInterface networkInterface;
    private final InetAddress group;
    private final SocketAddress localAddress;

    private InetSocketAddress serverAddress;

    private final ResponseFactory responseFactory;
    private final int multicastServerPort;
    private final boolean isServer;

    public MulticastChannelContext(DatagramChannel channel, Selector selector, NetworkInterface networkInterface, InetAddress group, SocketAddress localAddress, ResponseFactory responseFactory, int multicastServerPort, boolean isServer) {
        this.channel = channel;
        this.selector = selector;
        this.networkInterface = networkInterface;
        this.group = group;
        this.localAddress = localAddress;
        this.responseFactory = responseFactory;
        this.multicastServerPort = multicastServerPort;
        this.isServer = isServer;
    }

    public DatagramChannel getChannel() {
        return channel;
    }

    public Selector getSelector() {
        return selector;
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public InetAddress getGroup() {
        return group;
    }

    public SocketAddress getLocalAddress() {
        return localAddress;
    }

    public ResponseFactory getResponseFactory() {
        return responseFactory;
    }

    public int getMulticastServerPort() {
        return multicastServerPort;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(InetSocketAddress serverAddress) {
        if (!isServer) {
            this.serverAddress = serverAddress;
        }
    }

    public boolean isServer() {
        return isServer;
    }
}
