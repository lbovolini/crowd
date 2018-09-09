package com.github.lbovolini.crowd.client.connection;

import com.github.lbovolini.crowd.client.Scheduler;
import com.github.lbovolini.crowd.common.host.HostDetails;
import com.github.lbovolini.crowd.common.message.Message;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

public class ClientInfo {

    private final byte[] hostDetails;
    private final AsynchronousSocketChannel channel;
    private final Scheduler scheduler;
    private final boolean keepConnection;

    public ClientInfo(HostDetails hostDetails, AsynchronousSocketChannel channel, Scheduler scheduler, boolean keepConnection) throws IOException {
        this.hostDetails = Message.serialize(hostDetails);
        this.channel = channel;
        this.scheduler = scheduler;
        this.keepConnection = keepConnection;
    }

    public byte[] getHostDetails() {
        return hostDetails;
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public boolean keepConnection() {
        return keepConnection;
    }
}
