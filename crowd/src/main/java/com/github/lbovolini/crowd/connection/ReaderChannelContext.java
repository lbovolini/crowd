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

public class ReaderChannelContext {

    private boolean closed;

    private final AsynchronousSocketChannel channel;
    private PartialMessage partialMessage;

    private final Connection connection;

    private static final ByteBufferPool readerBufferPool = new ByteBufferPool();

    private static final ReaderChannelHandler READER_CHANNEL_HANDLER = new ReaderChannelHandler();

    private final ReentrantLock readLock;

    private final Queue<ByteBuffer> readerBufferQueue;

    private final ByteBuffer[] readerBufferArray;

    public ReaderChannelContext(AsynchronousSocketChannel channel, Connection connection) {
        this.channel = channel;
        this.connection = connection;
        this.partialMessage = new PartialMessage();
        this.readLock = new ReentrantLock();
        this.readerBufferQueue = new LinkedList<>();
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

    public ReentrantLock getReadLock() {
        return readLock;
    }

    public Queue<ByteBuffer> getReaderBufferQueue() {
        return readerBufferQueue;
    }

    public ByteBuffer[] getReaderBufferArray() {
        return readerBufferArray;
    }

    public boolean read() {

        ByteBuffer byteBuffer = readerBufferPool.poll();

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
        channel.read(readerBufferArray, 0, 1, 0, TimeUnit.SECONDS, this, READER_CHANNEL_HANDLER);

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
