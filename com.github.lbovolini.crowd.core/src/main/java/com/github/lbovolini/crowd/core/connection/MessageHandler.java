package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.request.Request;
import com.github.lbovolini.crowd.core.request.RequestQueue;

public class MessageHandler {

    private final Connection connection;
    private final RequestQueue requestQueue;

    public MessageHandler(Connection connection, RequestQueue requestQueue) {
        this.connection = connection;
        this.requestQueue = requestQueue;
    }

    public void handle(Message message) {
        requestQueue.enqueue(new Request(connection, message));
    }
}
