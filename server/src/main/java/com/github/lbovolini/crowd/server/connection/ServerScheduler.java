package com.github.lbovolini.crowd.server.connection;

import com.github.lbovolini.crowd.common.connection.Connection;
import com.github.lbovolini.crowd.common.host.HostDetails;
import com.github.lbovolini.crowd.common.message.Message;
import com.github.lbovolini.crowd.common.message.response.Response;
import com.github.lbovolini.crowd.server.node.NodeService;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerScheduler {

    private final Thread thread;
    private final NodeService nodeService;

    private static BlockingDeque<ConnectionMessage> responses;

    public ServerScheduler(NodeService nodeService) {
        this.nodeService = nodeService;
        thread = new Thread(() -> dispatch());
        responses = new LinkedBlockingDeque<>();
    }

    public void start() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public void enqueue(Connection connection, Message message) {
        responses.offer(new ConnectionMessage(connection, message));
    }

    public void dispatch() {
        while (true) {
            try {
                ConnectionMessage connectionMessage = responses.take();
                Message message = connectionMessage.getMessage();

                Object object = Message.deserialize(message.getData());

                // Response
                if (object instanceof Response) {
                    nodeService.reply((Response)object);
                }
                // HostDetails
                else if (object instanceof HostDetails) {
                    nodeService.join((HostDetails)object, connectionMessage.getConnection());
                }
                // String
                else if (object instanceof String) {
                    nodeService.leave((String)object);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
