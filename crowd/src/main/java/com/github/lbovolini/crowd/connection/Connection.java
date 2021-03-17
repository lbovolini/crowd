package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.message.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class Connection {

    private long hostId;

    private final AsynchronousSocketChannel channel;
    private final ReaderChannel readerChannel;
    private final WriterChannel writerChannel;

    public Connection(AsynchronousSocketChannel channel, ReaderChannel readerChannel, WriterChannel writerChannel) {
        this.channel = channel;
        this.readerChannel = readerChannel;
        this.writerChannel = writerChannel;
    }


    public long getHostId() {
        try {
            InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
            String host = address.getAddress().getHostAddress().replace(".", "");
            String port = Integer.toString(address.getPort());
            hostId = Long.parseLong(host + port);
        } catch (IOException e) { e.printStackTrace(); }
        return hostId;
    }

    public String getRemoteAddress() {
        try {
            return ((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress();
        } catch (IOException e) { e.printStackTrace(); }
        return "";
    }

    public int getRemotePort() {
        try {
            return ((InetSocketAddress) channel.getRemoteAddress()).getPort();
        } catch (IOException e) { e.printStackTrace(); }
        return 0;
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public void send(Message message) {
        writerChannel.write(message.getType(), message.getData());
    }


    public void receive() {
        readerChannel.read();
    }

    public void close() throws IOException {
        readerChannel.close();
        writerChannel.close();
    }

}
