package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.buffer.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class WriterChannel {

    private final AsynchronousSocketChannel channel;
    private final WriterChannelContext context;

    public WriterChannel(WriterChannelContext context) {
        this.channel = context.getChannel();
        this.context = context;
    }

    public boolean write(byte type, byte[] data) {

        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(type, data, WriterChannelContext.getWriterBufferPool());

        Lock writeLock = context.getWriteLock();
        writeLock.lock();

        try {
            if (isClosed()) { return false; }

            boolean wasEmpty = context.getWriterBufferQueue().isEmpty();
            context.getWriterBufferQueue().addAll(Arrays.asList(byteBufferArray));

            if (!wasEmpty) { return true; }
        }
        finally {
            writeLock.unlock();
        }

        context.setWriterBufferArray(byteBufferArray);
        channel.write(context.getWriterBufferArray(), 0, byteBufferArray.length, 0, TimeUnit.SECONDS, context, WriterChannelContext.getWriterChannelHandler());

        return true;
    }


    public boolean isClosed() {
        return context.isClosed();
    }

    public void close() throws IOException {
        context.close();
    }
}
