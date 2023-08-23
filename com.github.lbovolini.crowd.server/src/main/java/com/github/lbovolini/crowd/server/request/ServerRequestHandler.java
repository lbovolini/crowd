package com.github.lbovolini.crowd.server.request;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageType;
import com.github.lbovolini.crowd.core.message.messages.JoinGroup;
import com.github.lbovolini.crowd.core.message.messages.Response;
import com.github.lbovolini.crowd.core.node.NodeGroup;
import com.github.lbovolini.crowd.core.request.Request;
import com.github.lbovolini.crowd.core.request.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsável por manipular as requisições recebidas pelo servidor.
 */
public class ServerRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ServerRequestHandler.class);

    private final NodeGroup<?> nodeGroup;

    public ServerRequestHandler(NodeGroup<?> nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    @Override
    public void handle(Request request) {

        Message message = request.getMessage();
        MessageType type = MessageType.get(message.getType());

        switch (type) {
            case JOIN -> join(request);
            //case LEAVE: {}
            case REPLY -> reply(request);
            default -> System.out.println("UNKNOWN MESSAGE TYPE");
        }
    }

    private void join(Request request) {
        try {
            JoinGroup joinGroup = (JoinGroup) Message.deserialize(request.getMessage().getData());
            nodeGroup.join(joinGroup.getCores(), request.getConnection());
        } catch (Exception e) {
            log.debug("Error while joining new client");
        }
    }

    private void reply(Request request) {
        try {
            Response response = (Response) Message.deserialize(request.getMessage().getData());
            nodeGroup.reply(response, request.getConnection());
        } catch (Exception e) {
            log.debug("Error while reply to server");
        }
    }
}
