package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelCompletionHandler;
import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelContext;
import com.github.lbovolini.crowd.core.connection.ReaderChannelCompletionHandler;
import com.github.lbovolini.crowd.core.connection.WriterChannelCompletionHandler;
import com.github.lbovolini.crowd.core.request.Scheduler;
import com.github.lbovolini.crowd.core.worker.WorkerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientConnectionChannelCompletionHandlerTest {

    @Mock
    private AsynchronousSocketChannel channel;
    @Mock
    private Scheduler scheduler;
    private ClientConnectionChannelContext context;
    private ClientConnectionChannelCompletionHandler handler = new ClientConnectionChannelCompletionHandler();;

    @BeforeEach
    void setUp() {
        context = new ClientConnectionChannelContext(channel, scheduler, 1);
    }

    @Test
    void shouldCallReadAndWrite() {
        handler.completed(null, context);

        verify(channel).read(any(ByteBuffer[].class), anyInt(), anyInt(), anyLong(), eq(TimeUnit.SECONDS), any(WorkerContext.class), any(ReaderChannelCompletionHandler.class));
        verify(channel).write(any(ByteBuffer[].class), anyInt(), anyInt(), anyLong(), eq(TimeUnit.SECONDS), any(WorkerContext.class), any(WriterChannelCompletionHandler.class));
    }

}