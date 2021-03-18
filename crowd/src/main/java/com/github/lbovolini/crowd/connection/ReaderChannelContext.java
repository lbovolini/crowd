package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.buffer.ByteBufferPool;
import com.github.lbovolini.crowd.message.PartialMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;


public class ReaderChannelContext {

    private boolean closed;

    private final ReentrantLock readLock;

    private final AsynchronousSocketChannel channel;

    private final PartialMessage partialMessage;
    private MessageHandler messageHandler;

    private ByteBuffer[] readerBufferArray;
    private final Queue<ByteBuffer> readerBufferQueue;

    private static final ByteBufferPool readerBufferPool = new ByteBufferPool();
    private static final ReaderChannelHandler readerChannelHandler = new ReaderChannelHandler();


    public ReaderChannelContext(AsynchronousSocketChannel channel) {
        this.channel = channel;
        this.partialMessage = new PartialMessage();
        this.readLock = new ReentrantLock();
        this.readerBufferQueue = new LinkedList<>();
        //this.readerBufferArray = new ByteBuffer[BUFFER_ARRAY_SIZE];
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public PartialMessage getPartialMessage() {
        return partialMessage;
    }

    public static ByteBufferPool getReaderBufferPool() {
        return readerBufferPool;
    }

    public ReentrantLock getReadLock() {
        return readLock;
    }

    public Queue<ByteBuffer> getReaderBufferQueue() {
        return readerBufferQueue;
    }

    public ByteBuffer[] getReaderBufferArray() {
        return readerBufferArray;
    }

    public void setReaderBufferArray(ByteBuffer[] readerBufferArray) {
        this.readerBufferArray = readerBufferArray;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() throws IOException {
        this.closed = true;
        channel.close();
    }

    public static ReaderChannelHandler getReaderChannelHandler() {
        return readerChannelHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
