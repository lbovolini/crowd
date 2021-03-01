package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.scheduler.MessageFrom;
import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class Connection {

    private long hostId;

    private final Scheduler scheduler;
    private final AsynchronousSocketChannel channel;
    private final ReaderChannelContext readerChannelContext;
    private final WriterChannelContext writerChannelContext;

    public Connection(AsynchronousSocketChannel channel, Scheduler scheduler) {
        this.channel = channel;
        this.scheduler = scheduler;
        this.readerChannelContext = new ReaderChannelContext(channel, this);
        this.writerChannelContext = new WriterChannelContext(channel);
    }


    public long getHostId() {
        try {
            InetSocketAddress address = (InetSocketAddress)this.channel.getRemoteAddress();
            String host = address.getAddress().getHostAddress().replace(".", "");
            String port = Integer.toString(address.getPort());
            this.hostId = Long.valueOf(host + port);
        } catch (IOException e) { e.printStackTrace(); }
        return this.hostId;
    }

    public String getRemoteAddress() {
        try {
            return ((InetSocketAddress)channel.getRemoteAddress()).getAddress().getHostAddress();
        } catch (IOException e) { e.printStackTrace(); }
        return "";
    }

    public int getRemotePort() {
        try {
            return ((InetSocketAddress)channel.getRemoteAddress()).getPort();
        } catch (IOException e) { e.printStackTrace(); }
        return 0;
    }

    public void send(Message message) {
        writerChannelContext.write(message.getType(), message.getData());
    }


    public void receive() {
        readerChannelContext.read();
    }

    public void handle(Message message) {
        scheduler.enqueue(new MessageFrom(this, message));
    };

    public void close() throws IOException {
        readerChannelContext.close();
        writerChannelContext.close();
    }

}
