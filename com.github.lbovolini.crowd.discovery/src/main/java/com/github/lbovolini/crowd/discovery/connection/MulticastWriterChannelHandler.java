package com.github.lbovolini.crowd.discovery.connection;

import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.worker.MulticastWorkerContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class MulticastWriterChannelHandler {

    private MulticastWriterChannelHandler() {}

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
