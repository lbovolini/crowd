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
    private static int cores = Runtime.getRuntime().availableProcessors();
    private boolean running = false;
    private AsynchronousSocketChannel channel = null;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
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
        channel.bind(new InetSocketAddress(host, port));
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

    public boolean isRunning() {
        return running;
    }

    public void setRunning() {
        this.running = true;
    }

    private void setLibURL(String libURL) {
        LIB_URL = libURL;
    }

    public static void main(String[] args) {

        int cores;
        if (args.length > 0) {
            cores = Integer.parseInt(args[0]);
            if (cores < 1 || cores > Client.cores) {
                System.out.println("Invalid core count");
                return;
            }
            Client.cores = cores;
        }

        System.out.println("Using " + Client.cores + " cores");

        String host = HOST_NAME;
        int port = PORT;


        Client client = new Client(host, port);
        client.startClient();


    }
}