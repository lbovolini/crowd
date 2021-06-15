package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.buffer.ByteBufferPool;
import com.github.lbovolini.crowd.core.message.PartialMessage;
import com.github.lbovolini.crowd.core.worker.WorkerContext;

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

    private ByteBuffer[] readerBufferArray;
    private final Queue<ByteBuffer> readerBufferQueue;

    private static final ByteBufferPool readerBufferPool = new ByteBufferPool();
    private static final ReaderChannelCompletionHandler READER_CHANNEL_COMPLETION_HANDLER = new ReaderChannelCompletionHandler();

    private static final IOChannelScheduler ioChannelScheduler = new IOChannelScheduler(new ReaderChannelHandler());

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

    public static ReaderChannelCompletionHandler getReaderChannelCompletionHandler() {
        return READER_CHANNEL_COMPLETION_HANDLER;
    }

    public boolean requestChannelRead(WorkerContext workerContext) {
        return ioChannelScheduler.enqueue(workerContext);
    }
}
