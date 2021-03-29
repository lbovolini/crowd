package com.github.lbovolini.crowd.core.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class ReaderChannel {

    private final AsynchronousSocketChannel channel;
    private final ReaderChannelContext context;

    public ReaderChannel(ReaderChannelContext context) {
        this.channel = context.getChannel();
        this.context = context;
    }

    public ReaderChannelContext getContext() {
        return context;
    }

    public void read() {

        ByteBuffer[] byteBufferArray = new ByteBuffer[1];
        byteBufferArray[0] = ReaderChannelContext.getReaderBufferPool().poll();

        Lock readLock = context.getReadLock();
        readLock.lock();

        try {
            if (isClosed()) { return; }

            final boolean wasEmpty = context.getReaderBufferQueue().isEmpty();
            context.getReaderBufferQueue().addAll(Arrays.asList(byteBufferArray));

            if (!wasEmpty) { return; }
        }
        finally {
            readLock.unlock();
        }

        context.setReaderBufferArray(byteBufferArray);
    }

    public boolean isClosed() {
        return context.isClosed();
    }

    public void close() throws IOException {
        context.close();
    }
}
