package com.github.lbovolini.crowd.core.node;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.object.RemoteObject;

/**
 * Representa os dispositivos remotos (nós).
 */
public class Node {

    private final int cores;
    private final Connection connection;

    private RemoteObject remoteObject;

    public Node(int cores, Connection connection) {
        this.cores = cores;
        this.connection = connection;
    }

    public long getId() {
        return connection.getHostId();
    }

    public int cores() {
        return cores;
    }

    public Connection getConnection() {
        return connection;
    }

    public RemoteObject getRemoteObject() {
        return remoteObject;
    }

    public void setRemoteObject(RemoteObject remoteObject) {
        this.remoteObject = remoteObject;
    }

    public void send(Message message) {
        this.connection.send(message);
    }
}
