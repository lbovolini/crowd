package com.github.lbovolini.crowd.group.connection;

import com.github.lbovolini.crowd.group.message.MulticastMessage;
import com.github.lbovolini.crowd.group.message.MulticastMessageType;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

import static com.github.lbovolini.crowd.configuration.Config.MULTICAST_IP;
import static com.github.lbovolini.crowd.configuration.Config.MULTICAST_PORT;

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

    public void writeGroup(byte type) {
        write(type, new InetSocketAddress(MULTICAST_IP, MULTICAST_PORT));
    }
}
