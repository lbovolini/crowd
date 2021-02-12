package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.scheduler.MessageFrom;
import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class Connection {

    private long hostId;

    private final Scheduler scheduler;
    private final AsynchronousSocketChannel channel;
    private final IOChannel ioChannel;

    public Connection(AsynchronousSocketChannel channel, Scheduler scheduler) {
        this.channel = channel;
        this.scheduler = scheduler;
        this.ioChannel = new IOChannel(channel, this);
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
        // !todo ?
        //if (message.getDataLength() > (BUFFER_SIZE - HEADER_SIZE)) {
        //    System.out.println("Message too big");
        //}

        ByteBuffer buffer = ioChannel.getWriterBufferPool().poll();
        buffer.put(message.getType()).putShort(message.getDataLength()).put(message.getData());
        buffer.flip();

        ioChannel.write(buffer);
    }


    public void receive() {
        ioChannel.read();
    }

    public void handle(Message message) {
        scheduler.enqueue(new MessageFrom(this, message));
    };

    public void close() throws IOException {
        ioChannel.close();
    }

}
