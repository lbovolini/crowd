package com.github.lbovolini.crowd.server.connection;

import java.nio.channels.AsynchronousServerSocketChannel;

public class ServerInfo {
    private final AsynchronousServerSocketChannel server;
    private final ServerScheduler scheduler;

    public ServerInfo(AsynchronousServerSocketChannel server, ServerScheduler scheduler) {
        this.server = server;
        this.scheduler = scheduler;
    }

    public AsynchronousServerSocketChannel getServer() {
        return server;
    }

    public ServerScheduler getScheduler() {
        return scheduler;
    }
}
