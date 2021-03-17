package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.scheduler.Request;
import com.github.lbovolini.crowd.scheduler.Scheduler;

public class MessageHandler {

    private final Scheduler scheduler;
    private final Connection connection;

    public MessageHandler(Scheduler scheduler, Connection connection) {
        this.scheduler = scheduler;
        this.connection = connection;
    }

    void handle(Message message) {
        scheduler.enqueue(new Request(connection, message));
    }
}
