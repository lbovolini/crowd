package com.github.lbovolini.crowd.client;

import com.github.lbovolini.crowd.client.connection.ClientInfo;
import com.github.lbovolini.crowd.client.handler.ConnectionHandler;
import com.github.lbovolini.crowd.common.group.CodebaseAndServerAddress;
import com.github.lbovolini.crowd.common.group.Multicast;
import com.github.lbovolini.crowd.common.host.HostDetails;
import com.github.lbovolini.crowd.common.configuration.Config;

import java.io.IOException;
import java.net.*;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class Client {

    private static String id;

    private final String host;
    private final int port;

    private static int cores = Runtime.getRuntime().availableProcessors();

    private boolean running;

    private Scheduler scheduler;
    private ExecutorService pool;

    AsynchronousSocketChannel channel = null;
    ExecutorService socketPool = Executors.newSingleThreadExecutor();
    AsynchronousChannelGroup asynchronousChannelGroup;

    ClientInfo clientInfo = null;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.id = host + String.valueOf(port);
        this.running = false;
        this.pool = Executors.newSingleThreadExecutor();
        try {
            asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(socketPool);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getId() {
        return id;
    }

    public static int getCores() {
        return cores;
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning() {
        running = true;
    }

    public void start(CodebaseAndServerAddress csa) {
        start(csa.getCodebase(), csa.getServerAddress(), csa.getServerPort());
    }

    public void start(String codebase, String serverAddress, int serverPort) {

        InetSocketAddress hostAddress = new InetSocketAddress(serverAddress, serverPort);
        HostDetails hostDetails = new HostDetails(id, host, port, cores);

        System.out.println(codebase);
        System.out.println(serverAddress);
        System.out.println(serverPort);

        if(isRunning()) {
            scheduler.stop();
            this.pool.shutdownNow();
            this.pool = Executors.newSingleThreadExecutor();
        }
        setRunning();

        this.pool.submit(() -> {

            try {

                channel = AsynchronousSocketChannel.open(asynchronousChannelGroup);

                scheduler = new Scheduler(codebase);
                clientInfo = new ClientInfo(hostDetails, channel, scheduler, true);

                //https://docs.oracle.com/javase/7/docs/api/java/net/StandardSocketOptions.html#SO_REUSEADDR
                //http://blog.stephencleary.com/2009/05/detection-of-half-open-dropped.html
                channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
                channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

                channel.bind(new InetSocketAddress(host, port));
            } catch (IOException e) {
                System.out.println("Address already in use");
            }
            //
            channel.connect(hostAddress, clientInfo, new ConnectionHandler());
        });

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

        String host = Config.HOST_NAME;
        int port = Config.PORT;

        Client client = new Client(host, port);

        Multicast multicast = new Multicast(true) {
            public void handle(CodebaseAndServerAddress csa) {
                client.start(csa);
            }
        };

        try {
            multicast.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}