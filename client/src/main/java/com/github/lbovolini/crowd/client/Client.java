package com.github.lbovolini.crowd.client;

import com.github.lbovolini.crowd.client.connection.ClientInfo;
import com.github.lbovolini.crowd.client.handler.ConnectionHandler;
import com.github.lbovolini.crowd.common.group.ServerDetails;
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

    AsynchronousSocketChannel channel = null;
    ExecutorService socketPool = Executors.newSingleThreadExecutor();
    AsynchronousChannelGroup asynchronousChannelGroup;

    ClientInfo clientInfo = null;

    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.id = host + String.valueOf(port);
        this.running = false;
        asynchronousChannelGroup = AsynchronousChannelGroup.withThreadPool(socketPool);
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

    public void start(ServerDetails csa) throws IOException {
        start(csa.getCodebase(), csa.getServerAddress(), csa.getServerPort(), csa.getLibURL());
    }

    public void start(String codebase, String serverAddress, int serverPort, String libURL) throws IOException {

        setLibURL(libURL);

        InetSocketAddress hostAddress = new InetSocketAddress(serverAddress, serverPort);
        HostDetails hostDetails = new HostDetails(id, host, port, cores);

        if (isRunning()) {
            channel.close();
            //scheduler.stop();
        } else {
            scheduler = new Scheduler(codebase);
            setRunning();
        }
        channel = AsynchronousSocketChannel.open(asynchronousChannelGroup);
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        if (!running) {
            channel.bind(new InetSocketAddress(host, port));
        }
        clientInfo = new ClientInfo(hostDetails, channel, scheduler, true);
        channel.connect(hostAddress, clientInfo, new ConnectionHandler());

    }

    private void setLibURL(String libURL) {
        System.setProperty("lib.url", libURL);
    }

    //fix liburl
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

        Multicast multicast = new Multicast(true) {
            public void handle(ServerDetails csa) {
                try {
                    new Client(host, port).start(csa);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            multicast.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}