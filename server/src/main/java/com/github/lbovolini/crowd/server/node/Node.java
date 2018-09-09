package com.github.lbovolini.crowd.server.node;

import com.github.lbovolini.crowd.common.host.HostDetails;
import com.github.lbovolini.crowd.common.message.Message;
import com.github.lbovolini.crowd.common.message.request.Request;
import com.github.lbovolini.crowd.common.connection.Connection;
import com.github.lbovolini.crowd.server.RemoteObject;

import java.io.IOException;

public class Node {
    private final HostDetails hostDetails;
    private final Connection connection;

    private RemoteObject ref;

    public Node(HostDetails hostDetails, Connection connection) {
        this.hostDetails = hostDetails;
        this.connection = connection;
    }

    public HostDetails getInfo() {
        return hostDetails;
    }

    public int cores() {
        return hostDetails.availableProcessors();
    }

    public RemoteObject getRef() {
        return ref;
    }

    public void setRef(RemoteObject ref) {
        this.ref = ref;
    }

    public void stop() {
        ref.stop();
    }

    public void closed() {
        ref.closed();
    }

    public void invoke(Request request) throws IOException {

        byte[] data = Message.serialize(request);
        Message message = Message.create(Message.Type.INVOKE.getType(), data);

        connection.send(message);
    }
}
