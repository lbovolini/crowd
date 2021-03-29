package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.buffer.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class WriterChannel {

    private final WriterChannelContext context;

    public WriterChannel(WriterChannelContext context) {
        this.context = context;
    }

    public WriterChannelContext getContext() {
        return context;
    }

    public boolean write(byte type, byte[] data) {

        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(type, data, WriterChannelContext.getWriterBufferPool());

        Lock writeLock = context.getWriteLock();
        writeLock.lock();

        try {
            if (isClosed()) { return false; }

            boolean wasEmpty = context.getWriterBufferQueue().isEmpty();
            context.getWriterBufferQueue().addAll(Arrays.asList(byteBufferArray));

            if (!wasEmpty) { return false; }
        }
        finally {
            writeLock.unlock();
        }

        context.setWriterBufferArray(byteBufferArray);
        return true;
    }


    public boolean isClosed() {
        return context.isClosed();
    }

    public void close() throws IOException {
        context.close();
    }
}
