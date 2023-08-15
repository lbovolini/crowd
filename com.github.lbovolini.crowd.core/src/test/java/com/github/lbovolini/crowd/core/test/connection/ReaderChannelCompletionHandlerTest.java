package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.connection.*;
import com.github.lbovolini.crowd.core.worker.WorkerContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class ReaderChannelCompletionHandlerTest {

    @Mock
    private AsynchronousSocketChannel channel;
    @Mock
    private MessageHandler messageHandler;
    @InjectMocks
    private ReaderChannelContext readerChannelContext;
    @InjectMocks
    private WriterChannelContext writerChannelContext;

    private static final ReaderChannelCompletionHandler handler = new ReaderChannelCompletionHandler();

    @Test
    void shouldCompleteWithSuccessWhenResultIsZero() {
        WorkerContext context = new WorkerContext(readerChannelContext, writerChannelContext, messageHandler);

        assertDoesNotThrow(() -> {
            // Should test ONLY this method
            handler.completed(0L, context);
        });
    }

    @Test
    void shouldCompleteWithSuccessWhenResultIsGreaterThanZero() {

        WorkerContext context = new WorkerContext(readerChannelContext, writerChannelContext, messageHandler);

        ByteBuffer[] byteBufferArray = new ByteBuffer[1];
        byteBufferArray[0] = ReaderChannelContext.getReaderBufferPool().poll();
        readerChannelContext.getReaderBufferQueue().addAll(Arrays.asList(byteBufferArray));
        readerChannelContext.setReaderBufferArray(byteBufferArray);

        doAnswer((e) -> {
            ByteBuffer[] buffers = e.getArgument(0);
            for(int i = 0; i < buffers.length; i++) {
                buffers[i].flip();
            }
            return null;
        }).when(channel).read(any(ByteBuffer[].class), eq(0), anyInt(), eq(0L), eq(TimeUnit.SECONDS), eq(context), any(WriterChannelCompletionHandler.class));

        channel.read(byteBufferArray,
                0,
                byteBufferArray.length,
                0,
                TimeUnit.SECONDS,
                context,
                WriterChannelContext.getWriterChannelCompletionHandler());

        // Should test ONLY this method
        handler.completed(1L, context);

        // should clear all buffers
        assertTrue(Arrays.stream(readerChannelContext.getReaderBufferArray()).allMatch(Objects::nonNull));
        assertEquals(1, readerChannelContext.getReaderBufferQueue().size());
    }

}