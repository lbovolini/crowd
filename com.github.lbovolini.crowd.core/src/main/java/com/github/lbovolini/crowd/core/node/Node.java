package com.github.lbovolini.crowd.core.node;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.object.RemoteObject;

/**
 * Representa os dispositivos remotos (nós).
 */
public class Node {

    private final long id;
    private final String address;
    private final int port;
    private final int cores;
    private final Connection connection;

    private RemoteObject remoteObject;

    public Node(int cores, Connection connection) {
        this.id = connection.getHostId();
        this.address = connection.getRemoteAddress();
        this.port = connection.getRemotePort();
        this.cores = cores;
        this.connection = connection;
    }

    public long getId() {
        return id;
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
