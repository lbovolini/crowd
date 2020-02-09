package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.PartialMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.lbovolini.crowd.configuration.Config.BUFFER_ARRAY_SIZE;

public class IOChannel {

    private boolean closed;

    private final AsynchronousSocketChannel channel;
    private PartialMessage partialMessage;

    private final Connection connection;

    private static final ByteBufferPool readerBufferPool = new ByteBufferPool();
    private static final ByteBufferPool writerBufferPool = new ByteBufferPool();

    private static final WriteHandler writeHandler = new WriteHandler();
    private static final ReadHandler readHandler = new ReadHandler();

    private final ReentrantLock writeLock;
    private final ReentrantLock readLock;

    private final Queue<ByteBuffer> writerBufferQueue;
    private final Queue<ByteBuffer> readerBufferQueue;

    private final ByteBuffer[] writerBufferArray;
    private final ByteBuffer[] readerBufferArray;

    public IOChannel(AsynchronousSocketChannel channel, Connection connection) {
        this.channel = channel;
        this.connection = connection;
        this.partialMessage = new PartialMessage();
        this.writeLock = new ReentrantLock();
        this.readLock = new ReentrantLock();
        this.writerBufferQueue = new LinkedList<>();
        this.readerBufferQueue = new LinkedList<>();
        this.writerBufferArray = new ByteBuffer[BUFFER_ARRAY_SIZE];
        this.readerBufferArray = new ByteBuffer[BUFFER_ARRAY_SIZE];
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public PartialMessage getPartialMessage() {
        return partialMessage;
    }

    public ByteBufferPool getReaderBufferPool() {
        return readerBufferPool;
    }

    public ByteBufferPool getWriterBufferPool() {
        return writerBufferPool;
    }

    public ReentrantLock getWriteLock() {
        return writeLock;
    }

    public ReentrantLock getReadLock() {
        return readLock;
    }

    public Queue<ByteBuffer> getWriterBufferQueue() {
        return writerBufferQueue;
    }

    public Queue<ByteBuffer> getReaderBufferQueue() {
        return readerBufferQueue;
    }

    public ByteBuffer[] getWriterBufferArray() {
        return writerBufferArray;
    }

    public ByteBuffer[] getReaderBufferArray() {
        return readerBufferArray;
    }

    public boolean read() {

        ByteBuffer byteBuffer = readerBufferPool.pool();

        readLock.lock();

        try {
            if (isClosed()) { return false; }

            final boolean wasEmpty = readerBufferQueue.isEmpty();
            readerBufferQueue.add(byteBuffer);

            if (!wasEmpty) { return true; }
        }
        finally {
            readLock.unlock();
        }

        readerBufferArray[0] = byteBuffer;
        channel.read(readerBufferArray, 0, 1, 0, TimeUnit.SECONDS, this, readHandler);

        return true;
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
        channel.write(writerBufferArray, 0, 1, 0, TimeUnit.SECONDS, this, writeHandler );

        return true;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() throws IOException {
        this.closed = true;
        channel.close();
    }

    public void handle(Message message) {
        connection.handle(message);
    }
}
