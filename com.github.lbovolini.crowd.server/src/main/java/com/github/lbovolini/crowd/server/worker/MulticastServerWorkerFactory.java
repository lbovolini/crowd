package com.github.lbovolini.crowd.server.worker;

import com.github.lbovolini.crowd.core.util.HostUtils;
import com.github.lbovolini.crowd.discovery.connection.MulticastChannelContext;
import com.github.lbovolini.crowd.discovery.connection.MulticastChannelFactory;
import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.message.ServerResponseFactory;
import com.github.lbovolini.crowd.discovery.request.MulticastServerDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.Objects;

public class MulticastServerWorkerFactory {

    private static final Logger log = LoggerFactory.getLogger(MulticastServerWorkerFactory.class);

    public static final String MULTICAST_IP = System.getProperty("multicast.ip", "225.4.5.6");
    public static final int MULTICAST_SERVER_PORT = Integer.parseInt(System.getProperty("multicast.server.port", String.valueOf(8000)));
    public static final String MULTICAST_INTERFACE_NAME = System.getProperty("multicast.interface", HostUtils.getNetworkInterfaceName());

    private MulticastServerWorkerFactory() {}

    public static MulticastServerWorker defaultWorker(String hostname, int port) {
        Selector selector = null;
        DatagramChannel channel = null;

        try {
            selector = Selector.open();
            var networkInterface = NetworkInterface.getByName(MULTICAST_INTERFACE_NAME);
            var group = InetAddress.getByName(MULTICAST_IP);
            var localAddress = new InetSocketAddress("0.0.0.0", MULTICAST_SERVER_PORT);

            channel = MulticastChannelFactory.initializedChannel(selector, networkInterface, group, localAddress);

            var responseFactory = new ServerResponseFactory(hostname, port);
            var channelContext = new MulticastChannelContext(channel, selector, networkInterface, group, localAddress, responseFactory, MULTICAST_SERVER_PORT, true);

            var connection = new MulticastConnection(channelContext, new MulticastServerDispatcher());

            return new MulticastServerWorker(connection);
        } catch (IOException e) {
            log.error("Error while creating multicast server datagram channel", e);
            onIOException(selector, channel);
            throw new UncheckedIOException(e);
        }
    }

    private static void onIOException(Selector selector, DatagramChannel channel) {

        if (Objects.nonNull(selector)) {
            try {
                selector.close();
            } catch (IOException e) {
                log.error("Error while closing selector", e);
                throw new UncheckedIOException(e);
            }
        }

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