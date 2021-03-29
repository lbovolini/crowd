package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.request.Request;
import com.github.lbovolini.crowd.core.request.Scheduler;

public class MessageHandler {

    private final Connection connection;
    private final Scheduler scheduler;

    public MessageHandler(Connection connection, Scheduler scheduler) {
        this.connection = connection;
        this.scheduler = scheduler;
    }

    void handle(Message message) {
        scheduler.enqueue(new Request(connection, message));
    }
}
