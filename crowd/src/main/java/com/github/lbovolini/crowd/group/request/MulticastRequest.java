package com.github.lbovolini.crowd.group.request;

import com.github.lbovolini.crowd.group.connection.MulticastConnection;
import com.github.lbovolini.crowd.group.message.MulticastMessage;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MulticastRequest that = (MulticastRequest) o;
        return Objects.equals(connection, that.connection) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connection, message);
    }
}
