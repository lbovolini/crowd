package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.buffer.BufferUtils;
import com.github.lbovolini.crowd.core.connection.MessageHandler;
import com.github.lbovolini.crowd.core.connection.ReaderChannelContext;
import com.github.lbovolini.crowd.core.connection.WriterChannelContext;
import com.github.lbovolini.crowd.core.connection.WriterChannelHandler;
import com.github.lbovolini.crowd.core.worker.WorkerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WriterChannelHandlerTest {

    @Mock
    private AsynchronousSocketChannel channel;
    @Mock
    private MessageHandler messageHandler;
    @InjectMocks
    private ReaderChannelContext readerChannelContext;
    @InjectMocks
    private WriterChannelContext writerChannelContext;
    private WorkerContext workerContext;

    @BeforeEach
    void setUp() {
        workerContext = new WorkerContext(readerChannelContext, writerChannelContext, messageHandler);
    }

    @Test
    void shouldCallWrite() {
        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage((byte) 1, new byte[] {1}, WriterChannelContext.getWriterBufferPool());

        writerChannelContext.setWriterBufferArray(byteBufferArray);

        WriterChannelHandler.handle(workerContext);

        verify(channel, only()).write(any(ByteBuffer[].class), eq(0), anyInt(), anyLong(), eq(TimeUnit.SECONDS), eq(workerContext), eq(WriterChannelContext.getWriterChannelCompletionHandler()));
    }
}