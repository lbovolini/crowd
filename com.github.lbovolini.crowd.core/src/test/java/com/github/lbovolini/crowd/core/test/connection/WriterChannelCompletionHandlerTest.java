package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.buffer.BufferUtils;
import com.github.lbovolini.crowd.core.connection.MessageHandler;
import com.github.lbovolini.crowd.core.connection.ReaderChannelContext;
import com.github.lbovolini.crowd.core.connection.WriterChannelCompletionHandler;
import com.github.lbovolini.crowd.core.connection.WriterChannelContext;
import com.github.lbovolini.crowd.core.message.MessageType;
import com.github.lbovolini.crowd.core.request.RequestQueue;
import com.github.lbovolini.crowd.core.worker.ChannelFactory;
import com.github.lbovolini.crowd.core.worker.WorkerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class WriterChannelCompletionHandlerTest {

    private AsynchronousSocketChannel channel;
    private AsynchronousServerSocketChannel serverSocketChannel;
    @Mock
    private MessageHandler messageHandler;
    @Mock
    private RequestQueue requestQueue;

    private static final WriterChannelCompletionHandler handler = new WriterChannelCompletionHandler();

    @BeforeEach
    void setUp() throws Exception {
        channel = ChannelFactory.initializedChannel(new InetSocketAddress(0));
        serverSocketChannel = ChannelFactory.initializedServerChannel(new InetSocketAddress(8787), 1);
        channel.connect(new InetSocketAddress(8787)).get();
    }

    @AfterEach
    void tearDown() throws Exception {
        channel.close();
        serverSocketChannel.close();
    }

    @Test
    void shouldCompleteWithSuccessWhenResultIsZero() {

        ReaderChannelContext readerChannelContext = new ReaderChannelContext(channel);
        WriterChannelContext writerChannelContext = new WriterChannelContext(channel);
        WorkerContext context = new WorkerContext(readerChannelContext, writerChannelContext, messageHandler);

        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(MessageType.HEARTBEAT.getType(), new byte[] {1}, WriterChannelContext.getWriterBufferPool());
        writerChannelContext.getWriterBufferQueue().addAll(Arrays.asList(byteBufferArray));
        writerChannelContext.setWriterBufferArray(byteBufferArray);
        assertDoesNotThrow(() -> {
            // Should test ONLY this method
            handler.completed(0L, context);
        });

    }

    @Test
    void shouldCompleteWithSuccessWhenResultIsGreaterThanZero() {

        ReaderChannelContext readerChannelContext = new ReaderChannelContext(channel);
        WriterChannelContext writerChannelContext = new WriterChannelContext(channel);
        WorkerContext context = new WorkerContext(readerChannelContext, writerChannelContext, messageHandler);

        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(MessageType.HEARTBEAT.getType(), new byte[] {1}, WriterChannelContext.getWriterBufferPool());
        writerChannelContext.getWriterBufferQueue().addAll(Arrays.asList(byteBufferArray));
        writerChannelContext.setWriterBufferArray(byteBufferArray);

        // Should test ONLY this method
        handler.completed(1L, context);

        // should clear all buffers
        assertTrue(Arrays.stream(writerChannelContext.getWriterBufferArray()).allMatch(Objects::isNull));
        assertTrue(writerChannelContext.getWriterBufferQueue().isEmpty());
    }

}