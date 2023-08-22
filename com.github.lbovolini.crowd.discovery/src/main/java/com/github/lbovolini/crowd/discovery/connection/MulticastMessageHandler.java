package com.github.lbovolini.crowd.discovery.connection;

import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.request.MulticastRequest;
import com.github.lbovolini.crowd.discovery.request.MulticastRequestQueue;

public class MulticastMessageHandler {

    private final MulticastConnection connection;
    private final MulticastRequestQueue requestQueue;

    public MulticastMessageHandler(MulticastConnection connection, MulticastRequestQueue requestQueue) {
        this.connection = connection;
        this.requestQueue = requestQueue;
    }

    public void handle(MulticastMessage message) {
        requestQueue.enqueue(new MulticastRequest(connection, message));
    }
}
