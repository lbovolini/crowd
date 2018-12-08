package com.github.lbovolini.crowd.common.group;

import com.github.lbovolini.crowd.common.classloader.Monitor;
import com.github.lbovolini.crowd.common.message.Message;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.lbovolini.crowd.common.configuration.Config.*;

public class Multicast {

    private Selector selector;

    private final boolean isClient;
    private final int port;
    private ServerDetails csa;

    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();
    private final Object lock = new Object();
    private long lastResponseTime = 0;
    private final Set<String> hosts = ConcurrentHashMap.newKeySet();

    public Multicast() {
        this.isClient = false;
        this.port = MULTICAST_PORT;
    }

    public Multicast(boolean isClient) {
        this.isClient = isClient;
        this.port = MULTICAST_CLIENT_PORT;
    }

    private class ResponseFrom {
        private final Object response;
        private final InetSocketAddress address;

        ResponseFrom(Object response, InetSocketAddress address) {
            this.response = response;
            this.address = address;
        }

        private Object getResponse() {
            return response;
        }

        private InetSocketAddress getAddress() {
            return address;
        }
    }

    private void responseFromTo(DatagramChannel channel, InetSocketAddress address, Object response) throws ClosedChannelException {
        ResponseFrom attach = new ResponseFrom(response, address);
        channel.register(selector, SelectionKey.OP_WRITE, attach);
    }

    public void handle(ServerDetails csa) {
    }

    private void handle(DatagramChannel channel, byte[] buffer, InetSocketAddress address) {

        try {
            updateLastResponseTime();
            Object object = Message.deserialize(buffer);

            if (object instanceof String) {
                String response = (String)object;
                // CODEBASE
                handle(getCodebaseInfo(response));
            }
            else if (object instanceof Byte) {
                Byte response = (Byte) object;

                if (isClient) {return; };

                // DISCOVER
                if (Objects.equals(response, DISCOVER)) {
                    responseFromTo(channel, address, getCodebase(true));
                    hosts.add(address.toString());
                }
                else if (Objects.equals(response, HEARTBEAT)) {
                    // HEARTBEAT
                    if (hosts.contains(address.toString())) {
                        responseFromTo(channel, address, HEARTBEAT);
                    // DISCOVER
                    } else {
                        hosts.add(address.toString());
                        responseFromTo(channel, address, getCodebase(true));
                    }
                }

            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private ServerDetails getCodebaseInfo(String response) {
        String[] info = response.split(";");

        if (info.length < 4) {
            throw new RuntimeException("Server response error");
        }
        csa = new ServerDetails(info[0], info[1], info[2], info[3], info[4]);
        return csa;
    }

    private void read(SelectionKey selectionKey) throws IOException {

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();

        ByteBuffer buffer = ByteBuffer.allocate(MULTICAST_BUFFER_SIZE);
        SocketAddress address = null;

        while (address == null) {
            address = channel.receive(buffer);
        }

        buffer.flip();
        handle(channel, buffer.array(), (InetSocketAddress)address);
    }

    private void write(SelectionKey selectionKey) throws IOException {
        DatagramChannel channel = (DatagramChannel) selectionKey.channel();

        ResponseFrom responseFrom = (ResponseFrom) selectionKey.attachment();
        InetSocketAddress address = responseFrom.getAddress();

        byte[] response = Message.serialize(responseFrom.getResponse());
        ByteBuffer buffer = ByteBuffer.wrap(response);

        while (buffer.hasRemaining()) {
            channel.send(buffer, address);
        }
        buffer.clear();

        channel.register(selector, SelectionKey.OP_READ);
    }


    private boolean isMyself(InetSocketAddress address) {
        if (address.getAddress().getHostName().equals(HOST_NAME)) {
            return (address.getPort() == MULTICAST_PORT);
        }
        return false;
    }

    private void startHeartbeat(DatagramChannel channel) {
        pool.scheduleWithFixedDelay(() -> {
            try {
                InetSocketAddress address = new InetSocketAddress(MULTICAST_IP, MULTICAST_PORT);
                ResponseFrom responseFrom = new ResponseFrom(DISCOVER, address);

                if (!isDownTimeExceeded()) {
                    address = new InetSocketAddress(csa.getServerAddress(), MULTICAST_PORT);
                    responseFrom = new ResponseFrom(HEARTBEAT, address);
                }
                channel.register(selector, SelectionKey.OP_WRITE, responseFrom);
                selector.wakeup();
            } catch (IOException e) { e.printStackTrace(); }
        }, 0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    private void change(DatagramChannel channel) {
        try {
            Monitor monitor = new Monitor("/home/lbovolini/app") {
                @Override
                public void onChange() {
                    InetSocketAddress address = new InetSocketAddress(MULTICAST_IP, MULTICAST_CLIENT_PORT);
                    ResponseFrom responseFrom = new ResponseFrom(getCodebase(false), address);

                    try {
                        channel.register(selector, SelectionKey.OP_WRITE, responseFrom);
                        selector.wakeup();
                    } catch (ClosedChannelException e) { e.printStackTrace(); }
                }
            };
            monitor.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateLastResponseTime() {
        synchronized (lock) {
            lastResponseTime = System.nanoTime();
        }
    }

    private boolean isDownTimeExceeded() {
        long downTime;
        synchronized (lock) {
            downTime = System.nanoTime() - lastResponseTime;
        }
        downTime = TimeUnit.NANOSECONDS.toSeconds(downTime);

        return downTime > MAX_DOWNTIME;
    }

    private String getCodebase(boolean reconnect) {
        return CODEBASE + SEPARATOR + HOST_NAME + SEPARATOR + PORT + SEPARATOR + LIB_URL + SEPARATOR + reconnect;
    }

    public void start() throws IOException {

        MembershipKey membershipKey = null;

        try {
            DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
            NetworkInterface networkInterface = NetworkInterface.getByName(MULTICAST_INTERFACE_NAME);

            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.bind(new InetSocketAddress(this.port));
            channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
            //channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
            channel.configureBlocking(false);

            if (isClient) {
                startHeartbeat(channel);
            } else {
                change(channel);
            }

            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            membershipKey = channel.join(group, networkInterface);

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) { continue; }

                Set selectedKeys = selector.selectedKeys();
                Iterator keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey)keyIterator.next();
                    keyIterator.remove();

                    if (!selectionKey.isValid()) { continue; }

                    if (selectionKey.isReadable()) {
                       read(selectionKey);
                    } else if (selectionKey.isWritable()) {
                        write(selectionKey);
                    }
                }
            }
        } finally {
            if (membershipKey != null) {
                membershipKey.drop();
            }
        }
    }

}
