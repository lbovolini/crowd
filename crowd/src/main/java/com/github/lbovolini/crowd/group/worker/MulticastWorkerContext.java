package com.github.lbovolini.crowd.group.worker;

import com.github.lbovolini.crowd.group.connection.MulticastChannelContext;
import com.github.lbovolini.crowd.group.connection.MulticastConnection;
import com.github.lbovolini.crowd.group.message.MulticastMessageHandler;

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
