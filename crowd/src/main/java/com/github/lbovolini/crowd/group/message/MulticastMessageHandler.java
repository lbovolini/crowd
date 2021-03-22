package com.github.lbovolini.crowd.group.message;

import com.github.lbovolini.crowd.group.request.MulticastRequest;
import com.github.lbovolini.crowd.group.request.MulticastScheduler;
import com.github.lbovolini.crowd.group.connection.MulticastConnection;

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
