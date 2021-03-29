package com.github.lbovolini.crowd.client.worker;

import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelContext;
import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class ClientWorker {

    private final AsynchronousSocketChannel channel;
    private final ClientConnectionChannelContext clientInfo;

    public ClientWorker(AsynchronousSocketChannel channel, ClientConnectionChannelContext clientInfo) {
        this.channel = channel;
        this.clientInfo = clientInfo;
    }

    public void connect(InetSocketAddress serverAddress) {
        ClientConnectionChannelHandler connectionChannelHandler = new ClientConnectionChannelHandler();
        channel.connect(serverAddress, clientInfo, connectionChannelHandler);
    }

    public void reconnect(InetSocketAddress serverAddress) throws IOException {

    }

    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }
}
