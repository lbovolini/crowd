package com.github.lbovolini.crowd.core.worker;

import com.github.lbovolini.crowd.core.connection.MessageHandler;
import com.github.lbovolini.crowd.core.connection.ReaderChannelContext;
import com.github.lbovolini.crowd.core.connection.WriterChannelContext;

import java.nio.channels.AsynchronousSocketChannel;

public class WorkerContext {

    private final ReaderChannelContext readerChannelContext;
    private final WriterChannelContext writerChannelContext;
    private final MessageHandler messageHandler;

    public WorkerContext(ReaderChannelContext readerChannelContext, WriterChannelContext writerChannelContext, MessageHandler messageHandler) {
        this.readerChannelContext = readerChannelContext;
        this.writerChannelContext = writerChannelContext;
        this.messageHandler = messageHandler;
    }

    public ReaderChannelContext getReaderChannelContext() {
        return readerChannelContext;
    }

    public WriterChannelContext getWriterChannelContext() {
        return writerChannelContext;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public AsynchronousSocketChannel getChannel() {
        return readerChannelContext.getChannel();
    }
}
