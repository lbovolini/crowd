package com.github.lbovolini.crowd.group;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import static com.github.lbovolini.crowd.configuration.Config.*;

public abstract class Multicast extends Thread implements MulticastService {

    protected Selector selector;

    private final boolean isServer;

    private final int port;
    private InetAddress group;
    private NetworkInterface networkInterface;

    protected DatagramChannel channel;
    private InetSocketAddress serverAddress;

    private final MulticastRequestHandler multicastRequestHandler;

    public Multicast(int port, MulticastRequestHandler multicastRequestHandler, boolean isServer) {
        this.port = port;
        this.multicastRequestHandler = multicastRequestHandler;
        this.isServer = isServer;
    }

    private void init() throws IOException {
        networkInterface = NetworkInterface.getByName(MULTICAST_INTERFACE_NAME);
        group = InetAddress.getByName(MULTICAST_IP);
        channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        // !TODO
        channel.bind(new InetSocketAddress("0.0.0.0", port));
        channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
        //channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, false);
        channel.configureBlocking(false);
        channel.join(group, networkInterface);
        channel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    public void run() {
        try (final DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET);
             final Selector selector = Selector.open()) {

            this.channel = channel;
            this.selector = selector;
            init();
            onInit();

            while (true) {
                if (!selector.isOpen()) { break; }
                if (selector.select() == 0) { continue; }
                handleSelectionKeys(selector.selectedKeys());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read(SelectionKey selectionKey) throws IOException {

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();

        int bufferSize = 1;

        if (!isServer) {
            bufferSize = MULTICAST_BUFFER_SIZE;
        }

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        SocketAddress address = null;
        while (address == null) {
            address = channel.receive(buffer);
        }
        buffer.flip();

        Message message = new Message(buffer.array(), buffer.limit(), address);
        Request request = new Request(new Connection(this), message);


        if (!isServer) {
            setServerAddress((InetSocketAddress) address);
        }

        onReceive();

        multicastRequestHandler.handle(request);
    }

    private void write(SelectionKey selectionKey) throws IOException {

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();
        Message message = (Message) selectionKey.attachment();
        InetSocketAddress address = message.getAddress();

        byte[] response = message.getData();
        ByteBuffer buffer = ByteBuffer.wrap(response);

        while (buffer.hasRemaining()) {
            channel.send(buffer, address);
        }
        buffer.clear();

        channel.register(selectionKey.selector(), SelectionKey.OP_READ);
    }


    /**
     * Envia mensagem somente para o servidor
     * @param type
     */
    public void send(String type) {
        response(Message.ofType(type, serverAddress));
        this.selector.wakeup();
    }

    /**
     * Envia mensagem para todos participantes do grupo
     * @param type
     */
    public void sendAll(String type) {
        response(Message.ofType(type, new InetSocketAddress(MULTICAST_IP, MULTICAST_PORT)));
        this.selector.wakeup();
    }

    public void sendToHost(String type, InetSocketAddress address) {
        response(Message.ofType(type, address));
        this.selector.wakeup();
    }

    private void handleSelectionKeys(final Set<SelectionKey> selectedKeys) throws IOException {
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey selectionKey = keyIterator.next();
            keyIterator.remove();

            if (!selectionKey.isValid()) { continue; }

            if (selectionKey.isReadable()) {
                read(selectionKey);
            } else if (selectionKey.isWritable()) {
                write(selectionKey);
            }
        }
    }

    private void response(Message message) {
        try {
            channel.register(selector, SelectionKey.OP_WRITE, message);
        } catch (ClosedChannelException e) { e.printStackTrace(); }
    }


    public void setServerAddress(InetSocketAddress address) {
        this.serverAddress =  address;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }
}
