package com.github.lbovolini.crowd.core.worker;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.nio.channels.*;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChannelFactory {

    private ChannelFactory() {}

    public static AsynchronousServerSocketChannel initializedServerChannel(InetSocketAddress localAddress, int poolSize) {

        AsynchronousServerSocketChannel serverChannel = null;
        ExecutorService pool = null;
        AsynchronousChannelGroup channelGroup = null;
        try {
            pool = Executors.newFixedThreadPool(poolSize);
            channelGroup = AsynchronousChannelGroup.withThreadPool(pool);
            serverChannel = AsynchronousServerSocketChannel.open(channelGroup);
            initializeServerChannel(serverChannel, localAddress);

            return serverChannel;
        } catch (IOException e) {
            onIOException(pool, channelGroup, serverChannel);
            throw new UncheckedIOException(e);
        }
    }

    public static AsynchronousSocketChannel initializedChannel(InetSocketAddress localAddress) {

        AsynchronousSocketChannel channel = null;

        try {
            channel = AsynchronousSocketChannel.open();
            initializeChannel(channel, localAddress);

            return channel;
        } catch (IOException e) {
            onIOException(channel);
            throw new UncheckedIOException(e);
        }

    }

    private static void initializeServerChannel(AsynchronousServerSocketChannel serverChannel, InetSocketAddress localAddress) throws IOException {
        serverChannel.bind(localAddress);
    }

    private static void initializeChannel(AsynchronousSocketChannel channel, InetSocketAddress localAddress) throws IOException {
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(localAddress);
    }

    private static void onIOException(ExecutorService pool, AsynchronousChannelGroup channelGroup, AsynchronousServerSocketChannel serverSocketChannel) {

        if (Objects.nonNull(pool)) {
            pool.shutdown();
        }

        if (Objects.nonNull(channelGroup)) {
            channelGroup.shutdown();
        }

        if (Objects.nonNull(serverSocketChannel)) {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static void onIOException(AsynchronousSocketChannel channel) {

        if (Objects.nonNull(channel)) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
