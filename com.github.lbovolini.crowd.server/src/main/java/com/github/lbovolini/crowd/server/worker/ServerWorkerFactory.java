package com.github.lbovolini.crowd.server.worker;

import com.github.lbovolini.crowd.core.connection.*;
import com.github.lbovolini.crowd.core.node.NodeGroup;
import com.github.lbovolini.crowd.core.request.RequestHandler;
import com.github.lbovolini.crowd.core.request.Scheduler;
import com.github.lbovolini.crowd.core.request.ServerRequestHandler;
import com.github.lbovolini.crowd.core.worker.ChannelFactory;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class ServerWorkerFactory {

    public static final String HOSTNAME = System.getProperty("hostname", "");
    public static final int PORT = Integer.parseInt(System.getProperty("port", String.valueOf(8081)));

    private ServerWorkerFactory() {}

    public static ServerWorker defaultWorker(final NodeGroup<?> nodeGroup) {

        InetSocketAddress localAddress = new InetSocketAddress(HOSTNAME, PORT);

        AsynchronousServerSocketChannel serverChannel = ChannelFactory.initializedServerChannel(localAddress, 1);

        RequestHandler requestHandler = new ServerRequestHandler(nodeGroup);
        Scheduler scheduler = new Scheduler(requestHandler);

        ServerConnectionChannelContext serverInfo = new ServerConnectionChannelContext(serverChannel, scheduler);

        return new ServerWorker(serverChannel, serverInfo);
    }

    // !todo logger
    private static void checkThreadPoolSize(int poolSize) {
        if (poolSize > Runtime.getRuntime().availableProcessors()) {
            System.out.println("WARNING: Pool size bigger then available processors");
        }
    }
}
