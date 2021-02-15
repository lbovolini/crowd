package com.github.lbovolini.crowd.node;

import com.github.lbovolini.crowd.configuration.Config;
import com.github.lbovolini.crowd.connection.ServerAttachment;
import com.github.lbovolini.crowd.connection.ServerConnectionHandler;
import com.github.lbovolini.crowd.scheduler.RequestHandler;
import com.github.lbovolini.crowd.scheduler.Scheduler;
import com.github.lbovolini.crowd.scheduler.ServerRequestHandler;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final Scheduler scheduler;
    private final NodeGroup<?> nodeGroup;
    private final InetSocketAddress inetSocketAddress;
    private final AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private final AsynchronousChannelGroup asynchronousChannelGroup;
    private final ExecutorService executorService;

    public Server(final NodeGroup<?> nodeGroup) throws IOException {
        this(nodeGroup, Config.HOST_NAME, Config.PORT, 1);
    }

    public Server(final NodeGroup<?> nodeGroup, final String hostname, final int port, int poolSize) throws IOException {
        checkThreadPoolSize(poolSize);
        this.nodeGroup = nodeGroup;
        inetSocketAddress = new InetSocketAddress(hostname, port);
        executorService = Executors.newFixedThreadPool(poolSize);
        asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(executorService);
        asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(asynchronousChannelGroup);
        RequestHandler requestHandler = new ServerRequestHandler(nodeGroup);
        scheduler = new Scheduler(requestHandler);
    }

    public void start() throws IOException {
        asynchronousServerSocketChannel.bind(inetSocketAddress);
        scheduler.start();

        ServerAttachment serverInfo = new ServerAttachment(asynchronousServerSocketChannel, scheduler);
        asynchronousServerSocketChannel.accept(serverInfo, new ServerConnectionHandler());
    }

    // !todo loggger
    private void checkThreadPoolSize(int poolSize) {
        if (poolSize > Runtime.getRuntime().availableProcessors()) {
            System.out.println("WARNING: Pool size bigger then available processors");
        }
    }
}
