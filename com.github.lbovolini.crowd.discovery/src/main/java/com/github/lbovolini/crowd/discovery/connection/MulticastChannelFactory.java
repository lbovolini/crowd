package com.github.lbovolini.crowd.discovery.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Objects;

public class MulticastChannelFactory {

    private static final Logger log = LoggerFactory.getLogger(MulticastChannelFactory.class);

    private MulticastChannelFactory() {}

    public static DatagramChannel initializedChannel(Selector selector, NetworkInterface networkInterface, InetAddress group, InetSocketAddress localAddress) {

        DatagramChannel channel = null;
        try { 
            channel = DatagramChannel.open(StandardProtocolFamily.INET);
            initialize(channel, selector, networkInterface, group, localAddress);

            return channel;
        } catch (IOException e) {
            closeChannel(channel);
            log.error("Error while creating datagram channel", e);
            throw new UncheckedIOException(e);
        }
    }

    private static void initialize(DatagramChannel channel, Selector selector, NetworkInterface networkInterface, InetAddress group, InetSocketAddress localAddress) throws IOException {
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(localAddress);
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
        channel.configureBlocking(false);
        channel.join(group, networkInterface);
        channel.register(selector, SelectionKey.OP_READ);
    }

    private static void closeChannel(DatagramChannel channel) {
        if (Objects.nonNull(channel)) {
            try {
                channel.close();
            } catch (IOException e) {
                log.error("Error while closing datagram channel", e);
                throw new UncheckedIOException(e);
            }
        }
    }
}
