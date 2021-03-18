package com.github.lbovolini.crowd.scheduler;

import com.github.lbovolini.crowd.connection.Connection;
import com.github.lbovolini.crowd.message.Message;

public class Request {

    private final Connection connection;
    private final Message message;

    public Request(Connection connection, Message message) {
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