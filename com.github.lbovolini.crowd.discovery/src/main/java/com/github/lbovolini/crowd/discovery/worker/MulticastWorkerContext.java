package com.github.lbovolini.crowd.discovery.worker;

import com.github.lbovolini.crowd.discovery.connection.MulticastChannelContext;
import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;

public class MulticastWorkerContext {

    private final MulticastChannelContext channelContext;
    private final MulticastConnection connection;
    //private final MulticastMessageHandler messageHandler;

    public MulticastWorkerContext(MulticastChannelContext channelContext, MulticastConnection connection) {
        this.channelContext = channelContext;
        this.connection = connection;
    }

    public MulticastChannelContext getChannelContext() {
        return channelContext;
    }

    public MulticastConnection getConnection() {
        return connection;
    }

}
