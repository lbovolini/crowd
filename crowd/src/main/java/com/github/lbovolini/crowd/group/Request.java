package com.github.lbovolini.crowd.group;

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
