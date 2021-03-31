package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.buffer.ByteBufferPool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class WriterChannelContext {

    private boolean closed;

    private final ReentrantLock writeLock;

    private final AsynchronousSocketChannel channel;

    private ByteBuffer[] writerBufferArray;
    private final Queue<ByteBuffer> writerBufferQueue;

    private static final ByteBufferPool WRITER_BUFFER_POOL = new ByteBufferPool();
    private static final WriterChannelCompletionHandler WRITER_CHANNEL_COMPLETION_HANDLER = new WriterChannelCompletionHandler();

    public WriterChannelContext(AsynchronousSocketChannel channel) {
        this.channel = channel;
        this.writeLock = new ReentrantLock();
        this.writerBufferQueue = new LinkedList<>();
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public static ByteBufferPool getWriterBufferPool() {
        return WRITER_BUFFER_POOL;
    }

    public ReentrantLock getWriteLock() {
        return writeLock;
    }

    public Queue<ByteBuffer> getWriterBufferQueue() {
        return writerBufferQueue;
    }

    public ByteBuffer[] getWriterBufferArray() {
        return writerBufferArray;
    }

    public void setWriterBufferArray(ByteBuffer[] writerBufferArray) {
        this.writerBufferArray = writerBufferArray;
    }

    public static WriterChannelCompletionHandler getWriterChannelCompletionHandler() {
        return WRITER_CHANNEL_COMPLETION_HANDLER;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() throws IOException {
        this.closed = true;
        this.channel.close();
    }
}
