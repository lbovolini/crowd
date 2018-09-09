package com.github.lbovolini.crowd.server.connection;

import com.github.lbovolini.crowd.common.message.Message;
import com.github.lbovolini.crowd.common.connection.Connection;

public class ConnectionMessage {
    private final Connection connection;
    private final Message message;

    public ConnectionMessage(Connection connection, Message message) {
        this.connection = connection;
        this.message = message;
    }

    public Connection getConnection() {
        return connection;
    }

    public Message getMessage() {
        return message;
    }
}
