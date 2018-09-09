package com.github.lbovolini.crowd.common.connection;

import com.github.lbovolini.crowd.common.configuration.Config;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ByteBufferPool {
    private final ConcurrentLinkedDeque<ByteBuffer> byteBufferDeque;

    public ByteBufferPool() {
        byteBufferDeque = new ConcurrentLinkedDeque<>();
    }

    public ByteBuffer pool() {
        ByteBuffer buffer = byteBufferDeque.poll();

        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(Config.BUFFER_SIZE);
        }

        return buffer;
    }

    public void offer(ByteBuffer byteBuffer) {
        byteBuffer.clear();
        byteBufferDeque.offer(byteBuffer);
    }
}
