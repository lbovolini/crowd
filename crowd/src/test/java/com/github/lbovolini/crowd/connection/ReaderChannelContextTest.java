package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.buffer.ByteBufferPool;
import com.github.lbovolini.crowd.message.PartialMessage;
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
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReaderChannelContextTest {

    @Mock
    private AsynchronousSocketChannel channel;

    @InjectMocks
    private ReaderChannelContext context;

    @Test
    void shouldReturnChannel() {
        // Should test ONLY this method
        AsynchronousSocketChannel socketChannel = context.getChannel();

        // Assertions
        assertEquals(channel, socketChannel);
    }

    @Test
    void shouldReturnPartialMessage() {
        // Should test ONLY this method
        PartialMessage partialMessage = context.getPartialMessage();

        // Assertions
        assertNotNull(partialMessage);
    }

    @Test
    void shouldReturnReaderBufferPool() {
        // Should test ONLY this method
        ByteBufferPool bufferPool = context.getReaderBufferPool();

        // Assertions
        assertNotNull(bufferPool);
    }

    @Test
    void shouldReturnReaderLock() {
        // Should test ONLY this method
        Lock lock = context.getReadLock();

        // Assertions
        assertNotNull(lock);
    }

    @Test
    void shouldReturnReaderBufferQueue() {
        // Should test ONLY this method
        Queue<ByteBuffer> queue = context.getReaderBufferQueue();

        // Assertions
        assertNotNull(queue);
    }

    @Test
    void shouldReturnNullReaderBufferArray() {
        // Should test ONLY this method
        ByteBuffer[] byteBufferArray = context.getReaderBufferArray();

        // Assertions
        assertNull(byteBufferArray);
    }

    @Test
    void shouldSetReaderBufferArray() {
        // Input
        ByteBuffer[] byteBufferArray = new ByteBuffer[BUFFER_ARRAY_SIZE];

        // Should test ONLY this method
        context.setReaderBufferArray(byteBufferArray);

        // Assertions
        assertEquals(byteBufferArray, context.getReaderBufferArray());
    }

    @Test
    void shouldNotBeClosed() {
        // Should test ONLY this method
        boolean closed = context.isClosed();

        // Assertions
        assertFalse(closed);
    }

    @Test
    void shouldCloseChannel() {
        try {
            // Should test ONLY this method
            context.close();

            // Assertions
            verify(channel, only()).close();
            assertTrue(context.isClosed());

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldReturnReaderChannelHandler() {
        // Should test ONLY this method
        ReaderChannelHandler handler = ReaderChannelContext.getReaderChannelHandler();

        // Assertions
        assertNotNull(handler);
    }

    @Test
    void shouldReturnNullMessageHandler() {
        // Should test ONLY this method
        MessageHandler messageHandler = context.getMessageHandler();

        // Assertions
        assertNull(messageHandler);
    }

    @Test
    void shouldSetMessageHandler() {
        // Input
        MessageHandler messageHandler = new MessageHandler(null, null);
        // Should test ONLY this method
        context.setMessageHandler(messageHandler);

        // Assertions
        assertEquals(messageHandler, context.getMessageHandler());
    }
}