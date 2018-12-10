package com.github.lbovolini.crowd.client;

import com.github.lbovolini.crowd.client.connection.ClientInfo;
import com.github.lbovolini.crowd.client.handler.ConnectionHandler;
import com.github.lbovolini.crowd.common.group.ClientMulticaster;
import com.github.lbovolini.crowd.common.group.ServerResponse;
import com.github.lbovolini.crowd.common.group.Multicaster;
import com.github.lbovolini.crowd.common.host.HostDetails;

import java.io.IOException;
import java.net.*;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.lbovolini.crowd.common.configuration.Config.*;

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

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.id = host + String.valueOf(port);
        this.running = false;
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

    public void start(ServerResponse csa) throws IOException {
        start(csa.getCodebase(), csa.getServerAddress(), csa.getServerPort(), csa.getLibURL());
    }

    public void reload(ServerResponse serverResponse) {
        setLibURL(serverResponse.getLibURL());
        scheduler.reload(serverResponse.getCodebase());
    }

    public void update(ServerResponse serverResponse) {
        setLibURL(serverResponse.getLibURL());
        scheduler.addURL(serverResponse.getCodebase());
    }

    public void start(String codebase, String serverAddress, int serverPort, String libURL) throws IOException {

        setLibURL(libURL);

        InetSocketAddress hostAddress = new InetSocketAddress(serverAddress, serverPort);
        HostDetails hostDetails = new HostDetails(id, host, port, cores);

        if (isRunning()) {
            channel.close();
            scheduler.addURL(codebase);
        } else {
            scheduler = new Scheduler(codebase);
            setRunning();
        }
        channel = AsynchronousSocketChannel.open(asynchronousChannelGroup);
        channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        if (!isRunning()) {
            channel.bind(new InetSocketAddress(host, port));
        }
        clientInfo = new ClientInfo(hostDetails, channel, scheduler, true);
        channel.connect(hostAddress, clientInfo, new ConnectionHandler());

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
        Multicaster multicaster = new ClientMulticaster() {
            public void handle(ServerResponse serverDetails) {
                super.handle(serverDetails);

                try {
                    String type = serverDetails.getType();

                    switch (type) {
                        case CONNECT:
                            client.start(serverDetails);
                            break;
                        case UPDATE:
                            client.update(serverDetails);
                            break;
                        case RELOAD:
                            client.reload(serverDetails);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        multicaster.start();

    }
}