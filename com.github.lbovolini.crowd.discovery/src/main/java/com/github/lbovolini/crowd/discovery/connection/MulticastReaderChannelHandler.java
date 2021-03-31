package com.github.lbovolini.crowd.discovery.connection;

import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.worker.MulticastWorkerContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Objects;

public class MulticastReaderChannelHandler {

    private MulticastReaderChannelHandler() {}

    public static void handle(SelectionKey selectionKey, MulticastWorkerContext context) throws IOException {

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();

        ByteBuffer buffer = ByteBuffer.allocate(context.getChannelContext().getBufferSize());

        InetSocketAddress address = (InetSocketAddress) channel.receive(buffer);

        if (Objects.isNull(address)) {
            buffer.clear();
            return;
        }

        buffer.flip();

        MulticastMessage message = new MulticastMessage(buffer.array(), buffer.limit(), address);

        context.getChannelContext().setServerAddress(address);
        context.getMessageHandler().handle(message);
    }
}
