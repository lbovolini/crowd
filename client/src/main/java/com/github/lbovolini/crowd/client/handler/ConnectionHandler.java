package com.github.lbovolini.crowd.client.handler;

import com.github.lbovolini.crowd.client.connection.ClientConnection;
import com.github.lbovolini.crowd.client.connection.ClientInfo;
import com.github.lbovolini.crowd.common.message.Message;

import java.nio.channels.CompletionHandler;

public class ConnectionHandler implements CompletionHandler<Void, ClientInfo> {

    public void completed(Void aVoid, ClientInfo clientInfo) {

        byte type = 1; // MessageUtils.Type.JOIN.getType(); //JOIN
        byte[] data = clientInfo.getHostDetails();

        Message message = Message.create(type, data);

        //ClientConnection sender = new ClientConnection(clientInfo.getChannel(), clientInfo.getScheduler());
        //sender.send(message, new WriteHandler());

        ClientConnection connection = new ClientConnection(clientInfo.getChannel(), clientInfo.getScheduler());
        connection.send(message);
        connection.receive();
    }

    public void failed(Throwable throwable, ClientInfo connection) {
        throwable.printStackTrace();
    }
}
