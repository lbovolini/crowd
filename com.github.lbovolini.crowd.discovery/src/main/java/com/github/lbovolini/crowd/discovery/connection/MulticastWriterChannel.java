package com.github.lbovolini.crowd.discovery.connection;

import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

public class MulticastWriterChannel {

    private MulticastChannelContext context;

    public MulticastWriterChannel(MulticastChannelContext context) {
        this.context = context;
    }

    public void write(byte type) {
        write(type, context.getServerAddress());
    }

    public void write(byte type, InetSocketAddress address) {
        MulticastMessage message = MulticastMessage.ofType(MulticastMessageType.get(type), address);
        try {
            context.getChannel().register(context.getSelector(), SelectionKey.OP_WRITE, message);
            context.getSelector().wakeup();
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    // !TODO
    public void writeGroup(byte type) {
        write(type, new InetSocketAddress(context.getGroup().getHostName(), 8000));
    }
}