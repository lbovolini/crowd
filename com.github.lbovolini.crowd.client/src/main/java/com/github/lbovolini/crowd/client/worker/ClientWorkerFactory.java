package com.github.lbovolini.crowd.client.worker;

import com.github.lbovolini.crowd.classloader.ClassLoaderContext;
import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelContext;
import com.github.lbovolini.crowd.client.request.ClientRequestHandler;
import com.github.lbovolini.crowd.core.request.RequestHandler;
import com.github.lbovolini.crowd.core.request.Scheduler;
import com.github.lbovolini.crowd.core.util.HostUtils;
import com.github.lbovolini.crowd.core.worker.ChannelFactory;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class ClientWorkerFactory {

    public static final int PORT = Integer.parseInt(System.getProperty("port", String.valueOf(8081)));

    private ClientWorkerFactory() {}

    public static ClientWorker defaultWorker(ClassLoaderContext classLoaderContext) {

        InetSocketAddress localAddress = new InetSocketAddress(HostUtils.getHostAddressName(), PORT);

        AsynchronousSocketChannel channel = ChannelFactory.initializedChannel(localAddress);

        RequestHandler requestHandler = new ClientRequestHandler(classLoaderContext);
        Scheduler scheduler = new Scheduler(requestHandler);

        int cores = Runtime.getRuntime().availableProcessors();
        ClientConnectionChannelContext clientInfo = new ClientConnectionChannelContext(channel, scheduler, cores);

        return new ClientWorker(channel, clientInfo);
    }

}
