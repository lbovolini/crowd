package com.github.lbovolini.crowd.client;

import com.github.lbovolini.crowd.client.connection.ClientInfo;
import com.github.lbovolini.crowd.client.handler.ConnectionHandler;
import com.github.lbovolini.crowd.common.host.HostDetails;
import com.github.lbovolini.crowd.common.configuration.Config;

import java.io.IOException;
import java.net.*;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//!TODO server offline
public final class Client {

    private static String id;

    private final String host;
    private final int port;

    private static int cores = Runtime.getRuntime().availableProcessors();

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.id = host + String.valueOf(port);
    }

    public static String getId() {
        return id;
    }

    public static int getCores() {
        return cores;
    }

    public void start() throws IOException {

        InetSocketAddress hostAddress = new InetSocketAddress(Config.SERVER_HOST, Config.SERVER_PORT);

        ExecutorService socketPool = Executors.newSingleThreadExecutor();

        AsynchronousChannelGroup asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(socketPool);
        AsynchronousSocketChannel channel = AsynchronousSocketChannel.open(asynchronousChannelGroup);

        HostDetails hostDetails = new HostDetails(id, host, port, cores);
        Scheduler scheduler = new Scheduler();
        ClientInfo clientInfo = new ClientInfo(hostDetails, channel, scheduler, true);

        try {
            //https://docs.oracle.com/javase/7/docs/api/java/net/StandardSocketOptions.html#SO_REUSEADDR
            //http://blog.stephencleary.com/2009/05/detection-of-half-open-dropped.html
            channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

            channel.bind(new InetSocketAddress(Config.CLIENT_HOST, Config.CLIENT_PORT));
        } catch (BindException be) {
            System.out.println("Address already in use");
        }
        channel.connect(hostAddress, clientInfo, new ConnectionHandler());
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

        String host = Config.CLIENT_HOST;
        int port = Config.CLIENT_PORT;

        Client client = new Client(host, port);

        try {
            client.start();
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}