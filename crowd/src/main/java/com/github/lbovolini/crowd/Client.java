package com.github.lbovolini.crowd;


import com.github.lbovolini.crowd.group.ClientMulticaster;
import com.github.lbovolini.crowd.group.ServerResponse;
import com.github.lbovolini.crowd.handler.ClientAttachment;
import com.github.lbovolini.crowd.handler.ClientConnectionHandler;
import com.github.lbovolini.crowd.scheduler.ClientRequestHandler;
import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import static com.github.lbovolini.crowd.configuration.Config.*;

public final class Client {

    private final String host;
    private final int port;
    private final InetSocketAddress address;
    private final int cores;

    private AsynchronousSocketChannel channel;

    public Client(String host, int port, int cores) {
        this.host = host;
        this.port = port;
        this.cores = cores;
        this.address = new InetSocketAddress(host, port);
        this.channel = null;
    }

    public void startClient() {
        Scheduler scheduler = new Scheduler(new ClientRequestHandler());
        scheduler.start();

        ClientMulticaster clientMulticaster = new ClientMulticaster() {
            @Override
            public void handle(ServerResponse response) {
                setLibURL(response.getLibURL());
                String type = response.getType();

                switch (type) {
                    case CONNECT:
                        scheduler.create(response.getCodebase());
                        connect(response, scheduler);
                        break;
                    case UPDATE:
                        scheduler.update(response.getCodebase());
                        break;
                    case RELOAD:
                        scheduler.reload(response.getCodebase());
                        break;
                }
            }
        };
        clientMulticaster.start();
    }

    public void initChannel() throws IOException {
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        channel.bind(address);
    }

    private void recreateChannel() {
        try {
            if (channel != null) {
                channel.close();
            }
            channel = AsynchronousSocketChannel.open();
            initChannel();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void connect(ServerResponse response, Scheduler scheduler) {
        recreateChannel();
        InetSocketAddress hostAddress = new InetSocketAddress(response.getServerAddress(), response.getServerPort());
        connect(hostAddress, scheduler);
    }

    public void connect(InetSocketAddress hostAddress, Scheduler scheduler) {
        ClientAttachment clientInfo = new ClientAttachment(channel, scheduler, cores);
        channel.connect(hostAddress, clientInfo, new ClientConnectionHandler());
    }

    private void setLibURL(String libURL) {
        LIB_URL = libURL;
    }

    public static void main(String[] args) {

        System.out.println("Using " + POOL_SIZE + " threads");

        Client client = new Client(HOST_NAME, PORT, POOL_SIZE);
        client.startClient();

    }
}