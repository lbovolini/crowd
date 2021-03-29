package com.github.lbovolini.crowd.server.worker;

import com.github.lbovolini.crowd.core.connection.ServerConnectionChannelContext;
import com.github.lbovolini.crowd.core.connection.ServerConnectionChannelCompletionHandler;

import java.nio.channels.AsynchronousServerSocketChannel;

public class ServerWorker {

    private final AsynchronousServerSocketChannel serverChannel;
    private final ServerConnectionChannelContext connectionChannelContext;

    public ServerWorker(AsynchronousServerSocketChannel serverChannel, ServerConnectionChannelContext connectionChannelContext) {
        this.serverChannel = serverChannel;
        this.connectionChannelContext = connectionChannelContext;
    }

    public void start() {
        serverChannel.accept(connectionChannelContext, new ServerConnectionChannelCompletionHandler());
    }
}
