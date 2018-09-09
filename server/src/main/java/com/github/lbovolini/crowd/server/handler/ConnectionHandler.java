package com.github.lbovolini.crowd.server.handler;

import com.github.lbovolini.crowd.server.connection.ServerConnection;
import com.github.lbovolini.crowd.server.connection.ServerInfo;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, ServerInfo> {

    public void completed(AsynchronousSocketChannel client, ServerInfo serverInfo) {

        serverInfo.getServer().accept(serverInfo, this);

        ServerConnection connection = new ServerConnection(serverInfo.getServer(), client, serverInfo.getScheduler());
        connection.receive();
    }

    public void failed(Throwable e, ServerInfo serverInfo) {
        e.printStackTrace();
    }
}