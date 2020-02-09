package com.github.lbovolini.crowd;

import com.github.lbovolini.crowd.configuration.Config;
import com.github.lbovolini.crowd.connection.ServerAttachment;
import com.github.lbovolini.crowd.connection.ServerConnectionHandler;
import com.github.lbovolini.crowd.node.NodeGroup;
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

    private Scheduler scheduler;
    private final NodeGroup nodeGroup;
    private final InetSocketAddress inetSocketAddress;
    private AsynchronousServerSocketChannel serverChannel;
    private AsynchronousChannelGroup group;

    public Server(NodeGroup nodeGroup) {
        this.nodeGroup = nodeGroup;
        inetSocketAddress = new InetSocketAddress(Config.HOST_NAME, Config.PORT);
    }

    public void start() throws IOException {
        //int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newSingleThreadExecutor();
        group = AsynchronousChannelGroup.withThreadPool(pool);
        serverChannel = AsynchronousServerSocketChannel.open(group);

        serverChannel.bind(inetSocketAddress);

        RequestHandler requestHandler = new ServerRequestHandler(nodeGroup);
        scheduler = new Scheduler(requestHandler);
        scheduler.start();
        ServerAttachment serverInfo = new ServerAttachment(serverChannel, scheduler);
        serverChannel.accept(serverInfo, new ServerConnectionHandler());

        //Thread.currentThread().join();
        //asynchronousChannelGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }
}
