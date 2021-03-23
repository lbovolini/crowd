package com.github.lbovolini.crowd.group.worker;

import com.github.lbovolini.crowd.group.*;
import com.github.lbovolini.crowd.group.connection.*;
import com.github.lbovolini.crowd.group.message.MulticastMessageHandler;
import com.github.lbovolini.crowd.group.request.MulticastClientRequestHandler;
import com.github.lbovolini.crowd.group.request.MulticastRequestHandler;
import com.github.lbovolini.crowd.group.request.MulticastScheduler;
import com.github.lbovolini.crowd.group.request.MulticastServerRequestHandler;
import com.github.lbovolini.crowd.utils.HostUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.Objects;

import static com.github.lbovolini.crowd.configuration.Config.*;

public class MulticastWorkerFactory {

    private MulticastWorkerFactory() {}

    public static MulticastClientWorker defaultClientWorker(CodebaseService codebaseService) {

        Selector selector = null;
        DatagramChannel channel = null;

        try {
            selector = Selector.open();
            NetworkInterface networkInterface = NetworkInterface.getByName(HostUtils.getNetworkInterface());
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

    public static MulticastServerWorker defaultServerWorker() {
        Selector selector = null;
        DatagramChannel channel = null;

        try {
            selector = Selector.open();
            NetworkInterface networkInterface = NetworkInterface.getByName(HostUtils.getNetworkInterface());
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            InetSocketAddress localAddress = new InetSocketAddress("0.0.0.0", MULTICAST_PORT);

            channel = MulticastChannelFactory.initializedChannel(selector, networkInterface, group, localAddress);

            MulticastChannelContext channelContext = new MulticastChannelContext(channel, selector, networkInterface, group, localAddress, true);

            MulticastRequestHandler requestHandler = new MulticastServerRequestHandler();
            MulticastScheduler scheduler = new MulticastScheduler(requestHandler);

            MulticastReaderChannel readerChannel = new MulticastReaderChannel(channelContext);
            MulticastWriterChannel writerChannel = new MulticastWriterChannel(channelContext);

            MulticastConnection connection = new MulticastConnection(readerChannel, writerChannel);

            MulticastMessageHandler messageHandler = new MulticastMessageHandler(connection, scheduler);

            MulticastWorkerContext workerContext = new MulticastWorkerContext(channelContext, connection, messageHandler);

            return new MulticastServerWorker(workerContext);
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
