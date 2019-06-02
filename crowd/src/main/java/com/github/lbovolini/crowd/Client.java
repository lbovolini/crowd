package com.github.lbovolini.crowd;

import com.github.lbovolini.crowd.group.ClientMulticaster;
import com.github.lbovolini.crowd.group.ServerResponse;
import com.github.lbovolini.crowd.connection.ClientAttachment;
import com.github.lbovolini.crowd.connection.ClientConnectionHandler;
import com.github.lbovolini.crowd.scheduler.ClientRequestHandler;
import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.net.URL;
import java.nio.channels.AsynchronousSocketChannel;
import static com.github.lbovolini.crowd.configuration.Config.*;

public final class Client {

    private final String host;
    private final int port;
    private final InetSocketAddress address;
    private final int cores;
    private final String classPath;
    private final String libPath;

    private AsynchronousSocketChannel channel;

    public Client(String host, int port, int cores, String classPath, String libPath) {
        this.host = host;
        this.port = port;
        this.cores = cores;
        this.classPath = classPath;
        this.libPath = libPath;
        this.address = new InetSocketAddress(host, port);
        this.channel = null;
    }

    public void start() {
        Scheduler scheduler = new Scheduler(new ClientRequestHandler(), this.classPath, this.libPath);
        scheduler.start();

        ClientMulticaster clientMulticaster = new ClientMulticaster() {
            @Override
            public void handle(ServerResponse response) {
                String type = response.getType();
                URL[] codebase = response.getCodebase();
                URL libURL = response.getLibURL();

                System.out.println(response.toString());

                switch (type) {
                    case CONNECT:
                        scheduler.create(codebase, libURL);
                        connect(response.getServerAddress(), scheduler);
                        break;
                    case UPDATE:
                        scheduler.update(codebase, libURL);
                        break;
                    case RELOAD:
                        scheduler.reload(codebase, libURL);
                        break;
                }
            }
        };
        clientMulticaster.start();
    }

    private void initChannel() throws IOException {
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


    private void connect(InetSocketAddress hostAddress, Scheduler scheduler) {
        recreateChannel();
        ClientAttachment clientInfo = new ClientAttachment(channel, scheduler, cores);
        channel.connect(hostAddress, clientInfo, new ClientConnectionHandler());
    }

    public static void main(String[] args) {
//
//        System.out.println("Using " + POOL_SIZE + " threads");
//
//        Client client = new Client("192.168.0.100", PORT, POOL_SIZE);
//        client.startClient();
//
        Client client = new Client(HOST_NAME, PORT, POOL_SIZE, null, LIB_PATH);
        client.start();
    }

}