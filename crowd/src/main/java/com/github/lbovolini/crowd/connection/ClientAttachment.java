package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.nio.channels.AsynchronousSocketChannel;

public class ClientAttachment {

    private final AsynchronousSocketChannel channel;
    private final Scheduler scheduler;
    private final Integer cores;

    public ClientAttachment(AsynchronousSocketChannel channel, Scheduler scheduler, int cores) {
        this.channel = channel;
        this.scheduler = scheduler;
        this.cores = cores;
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Integer getCores() {
        return cores;
    }
}
