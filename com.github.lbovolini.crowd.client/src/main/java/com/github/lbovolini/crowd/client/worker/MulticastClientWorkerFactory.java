package com.github.lbovolini.crowd.client.worker;

import com.github.lbovolini.crowd.core.util.HostUtils;
import com.github.lbovolini.crowd.discovery.service.CodebaseService;
import com.github.lbovolini.crowd.discovery.connection.*;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageHandler;
import com.github.lbovolini.crowd.discovery.request.MulticastClientRequestHandler;
import com.github.lbovolini.crowd.discovery.request.MulticastRequestHandler;
import com.github.lbovolini.crowd.discovery.request.MulticastScheduler;
import com.github.lbovolini.crowd.discovery.worker.MulticastWorkerContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.Objects;

public class MulticastClientWorkerFactory {

    public static final String MULTICAST_IP = System.getProperty("multicast.ip", "225.4.5.6");
    public static final String MULTICAST_INTERFACE_NAME = System.getProperty("multicast.interface", HostUtils.getNetworkInterfaceName());
    public static final int MULTICAST_CLIENT_PORT = Integer.parseInt(System.getProperty("multicast.port", String.valueOf(8011)));

    private MulticastClientWorkerFactory() {}

    public static MulticastClientWorker defaultClientWorker(CodebaseService codebaseService) {

        Selector selector = null;
        DatagramChannel channel = null;

        try {
            selector = Selector.open();
            NetworkInterface networkInterface = NetworkInterface.getByName(MULTICAST_INTERFACE_NAME);
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            InetSocketAddress localAddress = new InetSocketAddress("0.0.0.0", MULTICAST_CLIENT_PORT);

            channel = MulticastChannelFactory.initializedChannel(selector, networkInterface, group, localAddress);

            MulticastChannelContext channelContext = new MulticastChannelContext(channel, selector, networkInterface, group, localAddress, false);

            MulticastRequestHandler requestHandler = new MulticastClientRequestHandler(codebaseService);
            MulticastScheduler scheduler = new MulticastScheduler(requestHandler);

            MulticastReaderChannel readerChannel = new MulticastReaderChannel(channelContext);
            MulticastWriterChannel writerChannel = new MulticastWriterChannel(channelContext);

            MulticastConnection connection = new MulticastConnection(readerChannel, writerChannel);

            MulticastMessageHandler messageHandler = new MulticastMessageHandler(connection, scheduler);

            MulticastWorkerContext workerContext = new MulticastWorkerContext(channelContext, connection, messageHandler);

            return new MulticastClientWorker(workerContext);
        } catch (IOException e) {
            onIOException(selector, channel);
            throw new UncheckedIOException(e);
        }
    }

    private static void onIOException(Selector selector, DatagramChannel channel) {

        if (Objects.nonNull(selector)) {
            try {
                selector.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (Objects.nonNull(channel)) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
