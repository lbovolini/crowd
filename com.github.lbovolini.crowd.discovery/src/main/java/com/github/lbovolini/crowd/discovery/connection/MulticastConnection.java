package com.github.lbovolini.crowd.discovery.connection;

import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.request.MulticastRequest;
import com.github.lbovolini.crowd.discovery.request.MulticastDispatcher;
import com.github.lbovolini.crowd.discovery.request.MulticastScheduler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Objects;

// !TODO concurrent read and write
// !TODO refactor write
// !TODO use nonblocking read and write
public class MulticastConnection {

    private final MulticastChannelContext context;
    private final MulticastScheduler scheduler;

    public MulticastConnection(MulticastChannelContext context, MulticastDispatcher requestHandler) {
        this.context = context;
        scheduler = new MulticastScheduler(requestHandler);
    }

    public void send(MulticastMessageType type) {
        send(type, context.getServerAddress());
    }

    public void send(MulticastMessageType type, InetSocketAddress address) {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("Receiver host address cannot be null");
        }

        var data = context.getResponseFactory().get(type);
        var message = new MulticastMessage(data, data.length, address);

        send(message);
    }

    public void multicastSend(MulticastMessageType type) {
        send(type, new InetSocketAddress(context.getGroup().getHostName(), context.getMulticastServerPort()));
    }

    private void send(MulticastMessage message) {
        try {
            context.getChannel().register(context.getSelector(), SelectionKey.OP_WRITE, message);
            context.getSelector().wakeup();
        } catch (ClosedChannelException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void receive() {
        try {
            context.getChannel().register(context.getSelector(), SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void read(DatagramChannel channel) throws IOException {

        var buffer = ByteBuffer.allocate(context.getBufferSize());
        var address = (InetSocketAddress) channel.receive(buffer);

        if (Objects.isNull(address)) {
            buffer.clear();
            return;
        }

        buffer.flip();

        var message = new MulticastMessage(buffer.array(), buffer.limit(), address);

        context.setServerAddress(address);
        handle(message);
    }

    public void write(DatagramChannel channel, MulticastMessage message) throws IOException {

        var address = message.getAddress();
        var response = message.getData();
        var buffer = ByteBuffer.wrap(response);

        var bytesSent = channel.send(buffer, address);
        buffer.clear();

        if (bytesSent == 0) {
            return;
        }

        this.receive();
    }


    public void handle(MulticastMessage message) {
        scheduler.enqueue(new MulticastRequest(this, message));
    }

    public Selector getSelector() {
        return context.getSelector();
    }

}
