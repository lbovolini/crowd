package com.github.lbovolini.crowd;

import com.github.lbovolini.crowd.classloader.Context;
import com.github.lbovolini.crowd.group.worker.MulticastClientWorker;
import com.github.lbovolini.crowd.connection.ClientConnectionChannelContext;
import com.github.lbovolini.crowd.connection.ClientConnectionChannelHandler;
import com.github.lbovolini.crowd.group.CodebaseService;
import com.github.lbovolini.crowd.group.worker.MulticastWorkerFactory;
import com.github.lbovolini.crowd.scheduler.ClientRequestHandler;
import com.github.lbovolini.crowd.scheduler.RequestHandler;
import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.URL;
import java.nio.channels.AsynchronousSocketChannel;
import static com.github.lbovolini.crowd.configuration.Config.*;

public final class Agent {

    private final InetSocketAddress hostAddress;
    private final int cores;
    private final String classPath;
    private final String libPath;

    private AsynchronousSocketChannel channel;


    private final Scheduler scheduler;

    public Agent(String host, int port, int cores, String classPath, String libPath) throws IOException {
        this.cores = cores;
        this.classPath = classPath;
        this.libPath = libPath;
        this.hostAddress = new InetSocketAddress(host, port);
        this.channel = AsynchronousSocketChannel.open();

        RequestHandler requestHandler = new ClientRequestHandler(new Context(classPath, libPath));
        this.scheduler = new Scheduler(requestHandler);
    }

    public void start() throws IOException {

        init();
        scheduler.start();

        final Context context = scheduler.getRequestHandler().getContext();

        CodebaseService codebaseService = new CodebaseService() {
            @Override
            public void onConnect(URL[] codebase, URL libURL, InetSocketAddress serverAddress) {
                try {
                    context.setClassURLs(codebase);
                    context.setLibURL(libURL);
                    reconnect(serverAddress);
                } catch (Exception e) { e.printStackTrace(); }
            }

            @Override
            public void onUpdate(URL[] codebase, URL libURL) {
                context.setClassURLs(codebase);
                context.setLibURL(libURL);
            }

            @Override
            public void onReload(URL[] codebase, URL libURL) {
                context.setClassURLs(codebase);
                context.setLibURL(libURL);
            }

        };

        MulticastClientWorker multicastClientWorker = MulticastWorkerFactory.defaultClientWorker(codebaseService);
        multicastClientWorker.start();
    }


    private void init() throws IOException {
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(hostAddress);
    }

    private void connect(InetSocketAddress serverAddress) {
        ClientConnectionChannelContext clientInfo = new ClientConnectionChannelContext(channel, scheduler, cores);
        ClientConnectionChannelHandler clientConnectionChannelHandler = new ClientConnectionChannelHandler();
        channel.connect(serverAddress, clientInfo, clientConnectionChannelHandler);
    }

    private void reconnect(InetSocketAddress serverAddress) throws IOException {
        close();
        channel = AsynchronousSocketChannel.open();
        init();
        connect(serverAddress);
    }

    private void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
    }


    public static void main(String[] args) throws IOException {

        Agent agent = new Agent(HOST_NAME, PORT, POOL_SIZE, null, LIB_PATH);
        agent.start();
    }
}