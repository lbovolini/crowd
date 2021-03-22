package com.github.lbovolini.crowd.group.request;

import com.github.lbovolini.crowd.group.connection.MulticastConnection;
import com.github.lbovolini.crowd.group.message.MulticastMessage;

public class MulticastRequest {

    private final MulticastConnection connection;
    private final MulticastMessage message;

    public MulticastRequest(MulticastConnection connection, MulticastMessage message) {
        this.connection = connection;
        this.message = message;
    }

    public MulticastConnection getConnection() {
        return connection;
    }

    public MulticastMessage getMessage() {
        return message;
    }
}
