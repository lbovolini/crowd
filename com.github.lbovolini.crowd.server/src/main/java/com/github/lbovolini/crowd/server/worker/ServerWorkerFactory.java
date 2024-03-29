package com.github.lbovolini.crowd.server.worker;

import com.github.lbovolini.crowd.core.connection.*;
import com.github.lbovolini.crowd.core.node.NodeGroup;
import com.github.lbovolini.crowd.core.request.RequestHandler;
import com.github.lbovolini.crowd.core.request.Scheduler;
import com.github.lbovolini.crowd.server.request.ServerRequestHandler;
import com.github.lbovolini.crowd.core.worker.ChannelFactory;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

public class ServerWorkerFactory {

    private ServerWorkerFactory() {}

    public static ServerWorker defaultWorker(final NodeGroup<?> nodeGroup, InetSocketAddress localAddress) {

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
