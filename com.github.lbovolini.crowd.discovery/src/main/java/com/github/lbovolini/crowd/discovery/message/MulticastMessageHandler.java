package com.github.lbovolini.crowd.discovery.message;

import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.request.MulticastRequest;
import com.github.lbovolini.crowd.discovery.request.MulticastScheduler;

public class MulticastMessageHandler {

    private final MulticastConnection connection;
    private final MulticastScheduler scheduler;

    public MulticastMessageHandler(MulticastConnection connection, MulticastScheduler scheduler) {
        this.connection = connection;
        this.scheduler = scheduler;
    }

    public void handle(MulticastMessage message) {
        scheduler.enqueue(new MulticastRequest(connection, message));
    }
}
