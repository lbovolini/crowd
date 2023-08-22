package com.github.lbovolini.crowd.discovery.connection;

import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.request.MulticastDispatcher;
import com.github.lbovolini.crowd.discovery.request.MulticastScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Objects;

// !TODO concurrent read and write
// !TODO refactor write
// !TODO use nonblocking read and write
public class MulticastConnection {

    private static final Logger log = LoggerFactory.getLogger(MulticastConnection.class);

    private final MulticastChannelContext context;
    private final MulticastMessageHandler messageHandler;

    public MulticastConnection(MulticastChannelContext context, MulticastDispatcher requestHandler) {
        this.messageHandler = new MulticastMessageHandler(this, new MulticastScheduler(requestHandler));
        this.context = context;
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
            log.error("Cannot listen for 'write ready' event in the channel because selector is closed", e);
            throw new UncheckedIOException(e);
        }
    }

    public void receive() {
        try {
            context.getChannel().register(context.getSelector(), SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            log.error("Cannot listen for 'read ready' event in the channel because selector is closed", e);
            throw new UncheckedIOException(e);
        }
    }

    public Selector getSelector() {
        return context.getSelector();
    }

    public MulticastMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public MulticastChannelContext getContext() {
        return context;
    }
}
