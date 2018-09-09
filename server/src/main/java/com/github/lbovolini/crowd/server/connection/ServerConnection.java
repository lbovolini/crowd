package com.github.lbovolini.crowd.server.connection;

import com.github.lbovolini.crowd.common.message.Message;
import com.github.lbovolini.crowd.common.connection.Connection;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

public class ServerConnection extends Connection {

    private final AsynchronousServerSocketChannel server;
    private final ServerScheduler scheduler;

    public ServerConnection(AsynchronousServerSocketChannel server, AsynchronousSocketChannel channel, ServerScheduler scheduler) {
        super(channel);
        this.server = server;
        this.scheduler = scheduler;
    }

    public AsynchronousServerSocketChannel getServer() {
        return server;
    }

    public void handle(Message message) {

        Message.Type messageType = Message.Type.get(message.getType());

        switch (messageType) {

            case REPLY:
                scheduler.enqueue(this, message);
                break;

            case JOIN:
                scheduler.enqueue(this, message);
                break;

            case LEAVE:
                scheduler.enqueue(this, message);
                break;

            default:
                System.out.println("UNKNOWN MESSAGE TYPE");
        }

    }
}