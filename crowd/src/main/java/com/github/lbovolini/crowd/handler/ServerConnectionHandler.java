package com.github.lbovolini.crowd.handler;

import com.github.lbovolini.crowd.connection.Connection;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, ServerAttachment> {

    public void completed(AsynchronousSocketChannel channel, ServerAttachment attachment) {

        attachment.getServerChannel().accept(attachment, this);

        Connection connection = new Connection(channel, attachment.getScheduler());
        connection.receive();
    }

    public void failed(Throwable e, ServerAttachment serverAttachment) {
        e.printStackTrace();
    }
}