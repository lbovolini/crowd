package com.github.lbovolini.crowd.group.connection;

import com.github.lbovolini.crowd.group.message.MulticastMessage;
import com.github.lbovolini.crowd.group.worker.MulticastWorkerContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class MulticastWriterChannelHandler {

    public static void handle(SelectionKey selectionKey, MulticastWorkerContext context) throws IOException {

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();
        MulticastMessage message = (MulticastMessage) selectionKey.attachment();
        InetSocketAddress address = message.getAddress();

        byte[] response = message.getData();
        ByteBuffer buffer = ByteBuffer.wrap(response);

        int bytesSent = channel.send(buffer, address);
        buffer.clear();

        if (bytesSent == 0) {
            return;
        }

        context.getConnection().receive();
    }
}
