package com.github.lbovolini.crowd.handler;

import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.nio.channels.AsynchronousServerSocketChannel;

public class ServerAttachment {
    private final AsynchronousServerSocketChannel serverChannel;
    private final Scheduler scheduler;

    public ServerAttachment(AsynchronousServerSocketChannel serverChannel, Scheduler scheduler) {
        this.serverChannel = serverChannel;
        this.scheduler = scheduler;
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
