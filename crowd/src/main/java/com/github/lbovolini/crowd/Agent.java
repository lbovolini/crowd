package com.github.lbovolini.crowd;

import com.github.lbovolini.crowd.group.ClientMulticaster;
import com.github.lbovolini.crowd.group.ServerResponse;
import com.github.lbovolini.crowd.connection.ClientAttachment;
import com.github.lbovolini.crowd.connection.ClientConnectionHandler;
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

    private final RequestHandler requestHandler;

    private final Scheduler scheduler;

    public Agent(String host, int port, int cores, String classPath, String libPath) throws IOException {
        this.cores = cores;
        this.classPath = classPath;
        this.libPath = libPath;
        this.hostAddress = new InetSocketAddress(host, port);
        this.channel = AsynchronousSocketChannel.open();
        this.requestHandler = new ClientRequestHandler();
        this.scheduler = new Scheduler(requestHandler, classPath, libPath);
    }

    public void start() throws IOException {

        init();
        scheduler.start();

        ClientMulticaster clientMulticaster = new ClientMulticaster() {
            @Override
            public void connect(URL[] codebase, URL libURL, InetSocketAddress serverAddress) {
                scheduler.create(codebase, libURL);
                try {
                    reconnect(serverAddress);
                } catch (IOException e) { e.printStackTrace(); }
            }

            @Override
            public void update(URL[] codebase, URL libURL) {
                scheduler.update(codebase, libURL);
            }

            @Override
            public void reload(URL[] codebase, URL libURL) {
                scheduler.reload(codebase, libURL);
            }
        };
        clientMulticaster.start();
    }


    private void init() throws IOException {
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(hostAddress);
    }

    private void connect(InetSocketAddress serverAddress) {
        ClientAttachment clientInfo = new ClientAttachment(channel, scheduler, cores);
        ClientConnectionHandler clientConnectionHandler = new ClientConnectionHandler();
        channel.connect(serverAddress, clientInfo, clientConnectionHandler);
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