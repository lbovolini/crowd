package com.github.lbovolini.crowd.core.request;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageType;
import com.github.lbovolini.crowd.core.message.messages.JoinGroup;
import com.github.lbovolini.crowd.core.message.messages.Response;
import com.github.lbovolini.crowd.core.node.NodeGroup;

import java.io.IOException;

/**
 * Responsável por manipular as requisições recebidas pelo servidor.
 */
public class ServerRequestHandler implements RequestHandler {

    private final NodeGroup nodeGroup;

    public ServerRequestHandler(NodeGroup nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    private static Object getObject(Message message) throws IOException, ClassNotFoundException {
        return Message.deserialize(message.getData());
    }

    @Override
    public void handle(Request request) throws Exception {

        Message message = request.getMessage();
        MessageType type = MessageType.get(message.getType());

        switch (type) {
            case JOIN: {
                join(request);
                break;
            }
            //case LEAVE: {
            //    leave(messageFrom);
            //    break;
            //}
            case REPLY: {
                reply(request);
                break;
            }
            default: {
                System.out.println("UNKNOWN MESSAGE TYPE");
            }
        }
    }

    @Override
    public void stop() {

    }

    private void join(Request request) throws IOException, ClassNotFoundException {
        JoinGroup joinGroup = (JoinGroup)getObject(request.getMessage());
        nodeGroup.join(joinGroup.getCores(), request.getConnection());
    }

    private void reply(Request request) throws IOException, ClassNotFoundException {
        Response response = (Response)getObject(request.getMessage());
        nodeGroup.reply(response, request.getConnection());
    }
}
