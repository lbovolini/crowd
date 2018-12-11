package com.github.lbovolini.crowd.server;

import com.github.lbovolini.crowd.common.configuration.Config;
import com.github.lbovolini.crowd.server.connection.ServerScheduler;
import com.github.lbovolini.crowd.server.node.NodeService;
import com.github.lbovolini.crowd.server.connection.ServerInfo;
import com.github.lbovolini.crowd.server.handler.ConnectionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final NodeService nodeService;
    private static ServerScheduler scheduler;

    public Server(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void start() throws IOException {

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(pool);
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(asynchronousChannelGroup);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(Config.HOST_NAME, Config.PORT);
        server.bind(inetSocketAddress);

        scheduler = new ServerScheduler(nodeService);
        scheduler.start();
        ServerInfo serverInfo = new ServerInfo(server, scheduler);
        server.accept(serverInfo, new ConnectionHandler());

        System.out.println("Server is listening");
        //Thread.currentThread().join();

        //asynchronousChannelGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }


}
