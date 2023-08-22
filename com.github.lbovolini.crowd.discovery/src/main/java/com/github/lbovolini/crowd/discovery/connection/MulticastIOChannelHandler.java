package com.github.lbovolini.crowd.discovery.connection;

import com.github.lbovolini.crowd.discovery.message.MulticastMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Objects;

public class MulticastIOChannelHandler {

    // !TODO cannot read/write message bigger than this size
    // !important Should NOT be bigger than datagram packet max size
    public static final int MULTICAST_BUFFER_SIZE = Integer.parseInt(System.getProperty("multicast.buffer.size", "1"));

    public static MulticastMessage read(DatagramChannel channel) throws IOException {

        var buffer = ByteBuffer.allocate(MULTICAST_BUFFER_SIZE);
        var address = (InetSocketAddress) channel.receive(buffer);

        if (Objects.isNull(address)) {
            buffer.clear();
            return null;
        }

        buffer.flip();

        return new MulticastMessage(buffer.array(), buffer.limit(), address);
    }

    public static boolean write(DatagramChannel channel, MulticastMessage message) throws IOException {

        var address = message.getAddress();
        var response = message.getData();
        var buffer = ByteBuffer.wrap(response);

        var bytesSent = channel.send(buffer, address);
        buffer.clear();

        return bytesSent != 0;
    }
}
