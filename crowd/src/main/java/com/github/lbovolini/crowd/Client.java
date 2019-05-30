package com.github.lbovolini.crowd;

import com.github.lbovolini.crowd.group.ClientMulticaster;
import com.github.lbovolini.crowd.group.ServerResponse;
import com.github.lbovolini.crowd.handler.ClientAttachment;
import com.github.lbovolini.crowd.handler.ClientConnectionHandler;
import com.github.lbovolini.crowd.scheduler.ClientRequestHandler;
import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
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

    public void startClient() {
        Scheduler scheduler = new Scheduler(new ClientRequestHandler(), this.classPath, this.libPath);
        scheduler.start();

        ClientMulticaster clientMulticaster = new ClientMulticaster() {
            @Override
            public void handle(ServerResponse response) {
                String type = response.getType();

                switch (type) {
                    case CONNECT:
                        try {
                            scheduler.create(toURLArray(response.getCodebase()), new URL(response.getLibURL()));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        connect(response, scheduler);
                        break;
                    case UPDATE:
                        scheduler.update(toURLArray(response.getCodebase()), response.getLibURL());
                        break;
                    case RELOAD:
                        scheduler.reload(toURLArray(response.getCodebase()), response.getLibURL());
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
        ClientConnectionHandler handler = new ClientConnectionHandler();
        channel.connect(hostAddress, clientInfo, handler);
    }

    public static void main(String[] args) {
//
//        System.out.println("Using " + POOL_SIZE + " threads");
//
//        Client client = new Client("192.168.0.100", PORT, POOL_SIZE);
//        client.startClient();
//
    }

    private static URL[] toURLArray(String codebase) {
        if (codebase == null || codebase.equals("")) {
            return null;
        }
        String[] strURL = codebase.split(" ");
        URL[] urls = new URL[strURL.length];

        for (int i = 0; i < strURL.length; i++) {
            try {
                urls[i] = new URL(strURL[i]);
            } catch (MalformedURLException e) { e.printStackTrace(); }
        }

        return urls;
    }
}