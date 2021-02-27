package com.github.lbovolini.crowd.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.lbovolini.crowd.configuration.Config.*;

public class WriterChannelContext {

    private boolean closed;

    private final AsynchronousSocketChannel channel;

    private static final ByteBufferPool writerBufferPool = new ByteBufferPool();

    private static final WriterChannelHandler WRITER_CHANNEL_HANDLER = new WriterChannelHandler();

    private final ReentrantLock writeLock;

    private final Queue<ByteBuffer> writerBufferQueue;

    private final ByteBuffer[] writerBufferArray;

    public WriterChannelContext(AsynchronousSocketChannel channel) {
        this.channel = channel;
        this.writeLock = new ReentrantLock();
        this.writerBufferQueue = new LinkedList<>();
        this.writerBufferArray = new ByteBuffer[BUFFER_ARRAY_SIZE];
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public boolean write(ByteBuffer byteBuffer) {

        writeLock.lock();

        try {
            if (isClosed()) { return false; }

            final boolean wasEmpty = writerBufferQueue.isEmpty();
            writerBufferQueue.add(byteBuffer);

            if (!wasEmpty) { return true; }
        }
        finally {
            writeLock.unlock();
        }

        writerBufferArray[0] = byteBuffer;
        channel.write(writerBufferArray, 0, 1, 0, TimeUnit.SECONDS, this, WRITER_CHANNEL_HANDLER );

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
}
