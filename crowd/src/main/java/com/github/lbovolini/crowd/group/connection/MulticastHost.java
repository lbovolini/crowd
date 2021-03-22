package com.github.lbovolini.crowd.group.connection;

public class MulticastHost {

    private final MulticastConnection connection;

    public MulticastHost(MulticastConnection connection) {
        this.connection = connection;
    }

    public MulticastConnection getConnection() {
        return connection;
    }
}
