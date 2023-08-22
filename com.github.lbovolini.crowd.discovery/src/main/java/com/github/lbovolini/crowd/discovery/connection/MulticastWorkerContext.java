package com.github.lbovolini.crowd.discovery.connection;

public class MulticastWorkerContext {

    private final MulticastChannelContext context;
    private final MulticastMessageHandler messageHandler;

    public MulticastWorkerContext(MulticastChannelContext context, MulticastMessageHandler messageHandler) {
        this.context = context;
        this.messageHandler = messageHandler;
    }

    public MulticastChannelContext getContext() {
        return context;
    }

    public MulticastMessageHandler getMessageHandler() {
        return messageHandler;
    }
}
