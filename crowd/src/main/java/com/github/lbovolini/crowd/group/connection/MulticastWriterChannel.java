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

    public boolean write(byte type) {
        return write(type, context.getServerAddress());
    }

    public boolean write(byte type, InetSocketAddress address) {
        MulticastMessage message = MulticastMessage.ofType(MulticastMessageType.get(type), address);
        try {
            context.getChannel().register(context.getSelector(), SelectionKey.OP_WRITE, message);
            context.getSelector().wakeup();
            return true;
        } catch (ClosedChannelException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeGroup(byte type) {
        return write(type, new InetSocketAddress(MULTICAST_IP, MULTICAST_PORT));
    }
}
