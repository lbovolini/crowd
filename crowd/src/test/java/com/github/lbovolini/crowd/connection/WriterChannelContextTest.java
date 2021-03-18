package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.buffer.ByteBufferPool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

import static com.github.lbovolini.crowd.configuration.Config.BUFFER_ARRAY_SIZE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WriterChannelContextTest {

    @Mock
    private AsynchronousSocketChannel channel;

    @InjectMocks
    private WriterChannelContext context;

    @Test
    void shouldCloseChannel() {

        try {
            // Should test ONLY this method
            context.close();

            // Assertions
            verify(channel, only()).close();
            assertTrue(context.isClosed());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldReturnChannel() {
        // Should test ONLY this method
        AsynchronousSocketChannel socketChannel = context.getChannel();

        // Assertions
        assertEquals(channel, socketChannel);
    }


    @Test
    void shouldReturnWriterBufferPool() {
        // Should test ONLY this method
        ByteBufferPool bufferPool = WriterChannelContext.getWriterBufferPool();

        // Assertions
        assertNotNull(bufferPool);
    }

    @Test
    void shouldReturnWriteLock() {
        // Should test ONLY this method
        Lock lock = context.getWriteLock();

        // Assertions
        assertNotNull(lock);
    }

    @Test
    void shouldReturnWriterBufferQueue() {
        // Should test ONLY this method
        Queue<ByteBuffer> queue = context.getWriterBufferQueue();

        // Assertions
        assertNotNull(queue);
    }

    @Test
    void shouldReturnNullWriterBufferArray() {
        // Should test ONLY this method
        ByteBuffer[] byteBufferArray = context.getWriterBufferArray();

        // Assertions
        assertNull(byteBufferArray);
    }

    @Test
    void shouldSetWriterBufferArray() {
        // Input
        ByteBuffer[] byteBufferArray = new ByteBuffer[BUFFER_ARRAY_SIZE];

        // Should test ONLY this method
        context.setWriterBufferArray(byteBufferArray);

        // Assertions
        assertEquals(byteBufferArray, context.getWriterBufferArray());
    }

    @Test
    void shouldReturnWriterChannelHandler() {
        // Should test ONLY this method
        WriterChannelHandler handler = WriterChannelContext.getWriterChannelHandler();

        // Assertions
        assertNotNull(handler);
    }

    @Test
    void shouldNotBeClosed() {
        // Should test ONLY this method
        boolean closed = context.isClosed();

        // Assertions
        assertFalse(closed);
    }
}