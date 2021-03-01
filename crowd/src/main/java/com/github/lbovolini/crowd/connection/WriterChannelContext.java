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

        int size = HEADER_SIZE + data.length;
        int neededBuffers = (int) Math.ceil(size / (double) BUFFER_SIZE);

        if (neededBuffers > BUFFER_ARRAY_SIZE || (neededBuffers * BUFFER_SIZE) > MAX_MESSAGE_SIZE) {
            throw new RuntimeException("Buffers are greater than MAX_MESSAGE_SIZE");
        }

        ByteBuffer[] neededBufferArray = new ByteBuffer[neededBuffers];

        int i = 0;
        while (i < neededBuffers) {
            neededBufferArray[i] = writerBufferPool.poll();
            i++;
        }


        short dataLength = (short) data.length;
        int writtenSum = Math.min(dataLength, BUFFER_SIZE - HEADER_SIZE);

        neededBufferArray[0].put(type).putShort(dataLength).put(data, 0, writtenSum);
        neededBufferArray[0].flip();

        for (int k = 1; k < neededBuffers; k++) {
            int newWritten = Math.min(dataLength - writtenSum, BUFFER_SIZE);
            neededBufferArray[k].put(data, writtenSum, newWritten);
            neededBufferArray[k].flip();
            writtenSum += newWritten;
        }

        writeLock.lock();

        try {
            if (isClosed()) { return false; }

            boolean wasEmpty = writerBufferQueue.isEmpty();
            writerBufferQueue.addAll(Arrays.asList(neededBufferArray));

            if (!wasEmpty) { return true; }
        }
        finally {
            writeLock.unlock();
        }

        setWriterBufferArray(neededBufferArray);
        channel.write(writerBufferArray, 0, neededBuffers, 0, TimeUnit.SECONDS, this, WRITER_CHANNEL_HANDLER);

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
