package com.github.lbovolini.crowd.group;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.lbovolini.crowd.configuration.Config.*;

public abstract class Multicaster extends Thread {

    protected Selector selector;

    private final int port;
    private InetAddress group;
    private NetworkInterface networkInterface;

    protected DatagramChannel channel;
    private final Set<String> hosts = ConcurrentHashMap.newKeySet();
    private InetSocketAddress serverAddress;

    public Multicaster(int port) {
        this.port = port;
    }

    public class ResponseFrom {
        private final String response;
        private final InetSocketAddress address;

        ResponseFrom(String  response, InetSocketAddress address) {
            this.response = response;
            this.address = address;
        }

        public String getResponse() {
            return response;
        }

        public InetSocketAddress getAddress() {
            return address;
        }
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
            scheduler();

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
        ByteBuffer buffer = ByteBuffer.allocate(MULTICAST_BUFFER_SIZE);

        SocketAddress address = null;
        while (address == null) {
            address = channel.receive(buffer);
        }

        buffer.flip();
        String message = getMessage(buffer);

        handle(message, (InetSocketAddress)address);
    }

    private void write(SelectionKey selectionKey) throws IOException {

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();
        ResponseFrom responseFrom = (ResponseFrom) selectionKey.attachment();
        InetSocketAddress address = responseFrom.getAddress();

        //!TODO
        byte[] response = responseFrom.getResponse().getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(response);
        while (buffer.hasRemaining()) {
            channel.send(buffer, address);
        }
        buffer.clear();

        channel.register(selectionKey.selector(), SelectionKey.OP_READ);
    }


    /**
     * Envia mensagem somente para o servidor
     * @param message
     */
    public void send(String message) {
        response(message, new InetSocketAddress(serverAddress.getAddress(), MULTICAST_PORT));
        this.selector.wakeup();
    }

    /**
     * Envia mensagem para todos participantes do grupo
     * @param message
     */
    public void sendAll(String message) {
        response(message, new InetSocketAddress(MULTICAST_IP, MULTICAST_PORT));
        this.selector.wakeup();
    }

    protected boolean isMyself(InetSocketAddress address) {
        if (address.getAddress().getHostName().equals(HOST_NAME)) {
            return (address.getPort() == MULTICAST_PORT);
        }
        return false;
    }

    /**
     * Adiciona endereço do Agent ao conjunto de endereços
     * @param address
     */
    protected void join(InetSocketAddress address) {
        hosts.add(address.toString());
    }

    /**
     * Verifica se o endereço está presente no conjunto de endereços
     * @param address
     * @return
     */
    protected boolean isMember(InetSocketAddress address) {
        return hosts.contains(address.toString());
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

    protected void setServerAddress(ServerResponse serverResponse) {
        this.serverAddress = serverResponse.getServerAddress();
    }

    protected void response(String message, InetSocketAddress destination) {
        ResponseFrom attach = new ResponseFrom(ResponseFactory.get(message), destination);
        try {
            channel.register(selector, SelectionKey.OP_WRITE, attach);
        } catch (ClosedChannelException e) { e.printStackTrace(); }
    }

    private void handle(ServerResponse serverResponse) {}

    /**
     * Se a resposta é maior do que 1, então foi enviada pelo servidor
     * @param response
     * @param address
     */
    protected void handle(String response, InetSocketAddress address) {

    }

    private static String getMessage(ByteBuffer buffer) {
        byte[] buff = new byte[buffer.limit()];
        buffer.get(buff, 0, buffer.limit());
        buffer.clear();
        return new String(buff, StandardCharsets.UTF_8);
    }

    protected abstract void scheduler();

}
