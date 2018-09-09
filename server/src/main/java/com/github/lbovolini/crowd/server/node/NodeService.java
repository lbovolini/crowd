package com.github.lbovolini.crowd.server.node;

import com.github.lbovolini.crowd.common.group.Group;
import com.github.lbovolini.crowd.common.host.HostDetails;
import com.github.lbovolini.crowd.common.message.response.Response;
import com.github.lbovolini.crowd.common.connection.Connection;

public class NodeService implements Group {

    private final NodeGroup nodeGroup;

    NodeService(NodeGroup nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    public void join(HostDetails hostDetails, Connection connection) {
        nodeGroup.join(hostDetails, connection);
    }

    public void leave(String hostAddress) {
        nodeGroup.leave(hostAddress);
    }

    public void reply(Response response) {
        nodeGroup.reply(response);
    }
}