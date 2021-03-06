package com.github.lbovolini.crowd.core.test.buffer;

import com.github.lbovolini.crowd.core.buffer.ByteBufferPool;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteBufferPoolTest {

    private final ByteBufferPool byteBufferPool = new ByteBufferPool();

    @Test
    void shouldReturnByteBufferWhenQueueIsEmpty() {

        // Should test ONLY this method
        ByteBuffer byteBuffer = byteBufferPool.poll();

        //assertEquals(ByteBuffer.allocateDirect(BUFFER_SIZE), byteBuffer);
    }

    @Test
    void shouldInsertByteBufferWhenQueueIsNotEmpty() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100);

        // Should test ONLY this method
        byteBufferPool.offer(byteBuffer);
        ByteBuffer byteBufferFromPool = byteBufferPool.poll();

        assertEquals(byteBuffer, byteBufferFromPool);
    }

    @Test
    void shouldClearByteBufferWhenOffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100);
        byteBuffer.position(1);
        byteBuffer.limit(1);

        // Should test ONLY this method
        byteBufferPool.offer(byteBuffer);

        assertEquals(0, byteBuffer.position());
        assertEquals(100, byteBuffer.limit());
    }

}