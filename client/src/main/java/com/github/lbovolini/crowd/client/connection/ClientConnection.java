package com.github.lbovolini.crowd.client.connection;


import com.github.lbovolini.crowd.client.Scheduler;
import com.github.lbovolini.crowd.common.message.Message;
import com.github.lbovolini.crowd.common.connection.Connection;

import java.nio.channels.AsynchronousSocketChannel;

public class ClientConnection extends Connection {

    private final Scheduler scheduler;

    public ClientConnection(AsynchronousSocketChannel channel, Scheduler scheduler) {
        super(channel);
        this.scheduler = scheduler;
        scheduler.start(this);
    }

    public void handle(Message message) {
        try {
            Message.Type messageType = Message.Type.get(message.getType());

            switch (messageType) {
                case INVOKE:
                    scheduler.enqueue(message);
                    break;
                default:
                    System.out.println("UNKNOWN MESSAGE TYPE");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}