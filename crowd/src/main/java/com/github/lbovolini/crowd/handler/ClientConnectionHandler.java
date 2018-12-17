package com.github.lbovolini.crowd.handler;

import com.github.lbovolini.crowd.connection.Connection;
import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.MessageFactory;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

public class ClientConnectionHandler implements CompletionHandler<Void, ClientAttachment> {

    public void completed(Void aVoid, ClientAttachment attachment) {


        Message message = null;
        try {
            message = MessageFactory.join(attachment.getCores());
        } catch (IOException e) { e.printStackTrace(); }


        Connection connection = new Connection(attachment.getChannel(), attachment.getScheduler());
        connection.send(message);
        connection.receive();
    }

    public void failed(Throwable throwable, ClientAttachment attachment) {
        throwable.printStackTrace();
    }
}
