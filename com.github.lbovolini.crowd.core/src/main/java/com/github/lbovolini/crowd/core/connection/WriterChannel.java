package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.buffer.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class WriterChannel {

    private final AsynchronousSocketChannel channel;
    private final WriterChannelContext context;

    public WriterChannel(WriterChannelContext context) {
        this.channel = context.getChannel();
        this.context = context;
    }

    public void write(byte type, byte[] data) {

        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(type, data, WriterChannelContext.getWriterBufferPool());

        Lock writeLock = context.getWriteLock();
        writeLock.lock();

        try {
            if (isClosed()) { return; }

            boolean wasEmpty = context.getWriterBufferQueue().isEmpty();
            context.getWriterBufferQueue().addAll(Arrays.asList(byteBufferArray));

            if (!wasEmpty) { return; }
        }
        finally {
            writeLock.unlock();
        }

        context.setWriterBufferArray(byteBufferArray);
    }


    public boolean isClosed() {
        return context.isClosed();
    }

    public void close() throws IOException {
        context.close();
    }
}
