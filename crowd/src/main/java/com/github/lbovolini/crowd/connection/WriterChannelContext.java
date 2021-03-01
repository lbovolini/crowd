package com.github.lbovolini.crowd.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class WriterChannelContext {

    private boolean closed;

    private final AsynchronousSocketChannel channel;

    private static final ByteBufferPool writerBufferPool = new ByteBufferPool();

    private static final WriterChannelHandler WRITER_CHANNEL_HANDLER = new WriterChannelHandler();

    private final ReentrantLock writeLock;

    private final Queue<ByteBuffer> writerBufferQueue;

    private ByteBuffer[] writerBufferArray;

    public WriterChannelContext(AsynchronousSocketChannel channel) {
        this.channel = channel;
        this.writeLock = new ReentrantLock();
        this.writerBufferQueue = new LinkedList<>();
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public boolean write(byte type, byte[] data) {

        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(type, data, writerBufferPool);

        writeLock.lock();

        try {
            if (isClosed()) { return false; }

            boolean wasEmpty = writerBufferQueue.isEmpty();
            writerBufferQueue.addAll(Arrays.asList(byteBufferArray));

            if (!wasEmpty) { return true; }
        }
        finally {
            writeLock.unlock();
        }

        setWriterBufferArray(byteBufferArray);
        channel.write(writerBufferArray, 0, byteBufferArray.length, 0, TimeUnit.SECONDS, this, WRITER_CHANNEL_HANDLER);

        return true;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() throws IOException {
        this.closed = true;
        channel.close();
    }

    public static ByteBufferPool getWriterBufferPool() {
        return writerBufferPool;
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
}
