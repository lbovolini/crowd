package com.github.lbovolini.crowd.group.connection;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

import static com.github.lbovolini.crowd.configuration.Config.MULTICAST_BUFFER_SIZE;

public class MulticastChannelContext {

    private final DatagramChannel channel;
    private final Selector selector;
    private final NetworkInterface networkInterface;
    private final InetAddress group;
    private final SocketAddress localAddress;

    private InetSocketAddress serverAddress;

    private final boolean isServer;
    private final int bufferSize;

    public MulticastChannelContext(DatagramChannel channel, Selector selector, NetworkInterface networkInterface, InetAddress group, SocketAddress localAddress, boolean isServer) {
        this.channel = channel;
        this.selector = selector;
        this.networkInterface = networkInterface;
        this.group = group;
        this.localAddress = localAddress;
        this.isServer = isServer;
        this.bufferSize = bufferSize();
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

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(InetSocketAddress serverAddress) {
        if (!isServer) {
            this.serverAddress = serverAddress;
        }
    }

    private int bufferSize() {
        if (!isServer) {
            return MULTICAST_BUFFER_SIZE;
        }
        return 1;
    }

    public int getBufferSize() {
        return bufferSize;
    }
}
