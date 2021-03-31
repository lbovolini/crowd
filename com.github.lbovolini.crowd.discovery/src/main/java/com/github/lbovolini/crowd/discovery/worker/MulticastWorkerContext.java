package com.github.lbovolini.crowd.discovery.worker;

import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageHandler;
import com.github.lbovolini.crowd.discovery.connection.MulticastChannelContext;

public class MulticastWorkerContext {

    private final MulticastChannelContext channelContext;
    private final MulticastConnection connection;
    private final MulticastMessageHandler messageHandler;

    public MulticastWorkerContext(MulticastChannelContext channelContext, MulticastConnection connection, MulticastMessageHandler messageHandler) {
        this.channelContext = channelContext;
        this.connection = connection;
        this.messageHandler = messageHandler;
    }

    public MulticastChannelContext getChannelContext() {
        return channelContext;
    }

    public MulticastConnection getConnection() {
        return connection;
    }

    public MulticastMessageHandler getMessageHandler() {
        return messageHandler;
    }
}
