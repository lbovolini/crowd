package com.github.lbovolini.crowd.group;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.lbovolini.crowd.configuration.Config.*;

public abstract class Multicaster {

    // !todo thread safe?
    protected Selector selector;

    private final int port;
    private final InetAddress group;
    private final NetworkInterface networkInterface;

    protected final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    private long lastResponseTime = 0;
    private final Object lock = new Object();

    private final Set<String> hosts = ConcurrentHashMap.newKeySet();
    protected InetSocketAddress allClients = new InetSocketAddress(MULTICAST_IP, MULTICAST_CLIENT_PORT);


    public Multicaster(int port) {
        this.port = port;
        try {
            networkInterface = NetworkInterface.getByName(MULTICAST_INTERFACE_NAME);
            group = InetAddress.getByName(MULTICAST_IP);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private class ResponseFrom {
        private final String response;
        private final InetSocketAddress address;

        ResponseFrom(String  response, InetSocketAddress address) {
            this.response = response;
            this.address = address;
        }

        private String getResponse() {
            return response;
        }

        private InetSocketAddress getAddress() {
            return address;
        }
    }

    protected void responseFromTo(String response, DatagramChannel channel, InetSocketAddress address) {
        ResponseFrom attach = new ResponseFrom(response, address);
        try {
            channel.register(selector, SelectionKey.OP_WRITE, attach);
        } catch (ClosedChannelException e) { e.printStackTrace(); }
    }

    public abstract void handle(ServerResponse serverResponse);

    protected abstract void handle(final DatagramChannel channel, String response, InetSocketAddress address);

    // !todo thread safe?
    protected abstract void startScheduler(final DatagramChannel channel);

    private void read(SelectionKey selectionKey) throws IOException {

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(MULTICAST_BUFFER_SIZE);

        SocketAddress address = null;
        while (address == null) {
            address = channel.receive(buffer);
        }

        buffer.flip();
        String message = getMessage(buffer);
        handle(channel, message, (InetSocketAddress)address);
    }

    private String getMessage(ByteBuffer buffer) {
        byte[] buff = new byte[buffer.limit()];
        buffer.get(buff, 0, buffer.limit());
        buffer.clear();
        return new String(buff, StandardCharsets.UTF_8);
    }

    private void write(SelectionKey selectionKey) throws IOException {

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();
        ResponseFrom responseFrom = (ResponseFrom) selectionKey.attachment();
        InetSocketAddress address = responseFrom.getAddress();

        byte[] response = responseFrom.getResponse().getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(response);
        while (buffer.hasRemaining()) {
            channel.send(buffer, address);
        }
        buffer.clear();

        channel.register(selectionKey.selector(), SelectionKey.OP_READ);
    }


    protected boolean isMyself(InetSocketAddress address) {
        if (address.getAddress().getHostName().equals(HOST_NAME)) {
            return (address.getPort() == MULTICAST_PORT);
        }
        return false;
    }

    // !todo thread safe?
    protected void wakeUp() {
        this.selector.wakeup();
    }


    protected void updateLastResponseTime() {
        synchronized (lock) {
            lastResponseTime = System.nanoTime();
        }
    }

    protected boolean isDownTimeExceeded() {
        long downTime;
        synchronized (lock) {
            downTime = System.nanoTime() - lastResponseTime;
        }
        downTime = TimeUnit.NANOSECONDS.toSeconds(downTime);

        return downTime > MAX_DOWNTIME;
    }


    protected void join(InetSocketAddress address) {
        hosts.add(address.toString());
    }

    protected boolean isMember(InetSocketAddress address) {
        return hosts.contains(address.toString());
    }

    public void start() {
        try (final DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
             final Selector selector = Selector.open()) {

            this.selector = selector;
            initChannel(channel, selector);
            startScheduler(channel);

            while (true) {
                if (!selector.isOpen()) { break; }
                if (selector.select() == 0) { continue; }
                handleSelectionKeys(selector.selectedKeys());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSelectionKeys(final Set selectedKeys) throws IOException {
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

    private void initChannel(final DatagramChannel channel, final Selector selector) throws IOException {
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        // !TODO
        channel.bind(new InetSocketAddress("0.0.0.0", this.port));
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, this.networkInterface);
        //channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
        channel.configureBlocking(false);
        channel.join(this.group, this.networkInterface);
        channel.register(selector, SelectionKey.OP_READ);
    }


}
