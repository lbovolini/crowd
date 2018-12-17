package com.github.lbovolini.crowd.scheduler;

import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.messages.JoinGroup;
import com.github.lbovolini.crowd.message.messages.Response;
import com.github.lbovolini.crowd.node.NodeGroup;

import java.io.IOException;

public class ServerRequestHandler implements RequestHandler {

    private final NodeGroup nodeGroup;

    public ServerRequestHandler(NodeGroup nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    private static Object getObject(Message message) throws IOException, ClassNotFoundException {
        return Message.deserialize(message.getData());
    }

    @Override
    public void handle(MessageFrom messageFrom) throws Exception {

        Message message = messageFrom.getMessage();
        Message.Type type = Message.Type.get(message.getType());

        switch (type) {
            case JOIN: {
                join(messageFrom);
                break;
            }
            //case LEAVE: {
            //    leave(messageFrom);
            //    break;
            //}
            case REPLY: {
                reply(messageFrom);
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


    private void join(MessageFrom messageFrom) throws IOException, ClassNotFoundException {
        JoinGroup joinGroup = (JoinGroup)getObject(messageFrom.getMessage());
        nodeGroup.join(joinGroup.getCores(), messageFrom.getConnection());
    }

    private void reply(MessageFrom messageFrom) throws IOException, ClassNotFoundException {
        Response response = (Response)getObject(messageFrom.getMessage());
        nodeGroup.reply(response, messageFrom.getConnection());
    }
}
