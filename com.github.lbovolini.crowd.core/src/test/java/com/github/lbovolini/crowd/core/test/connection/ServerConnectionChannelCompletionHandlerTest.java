package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.connection.ReaderChannelCompletionHandler;
import com.github.lbovolini.crowd.core.connection.ServerConnectionChannelCompletionHandler;
import com.github.lbovolini.crowd.core.connection.ServerConnectionChannelContext;
import com.github.lbovolini.crowd.core.request.Scheduler;
import com.github.lbovolini.crowd.core.worker.WorkerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServerConnectionChannelCompletionHandlerTest {

    @Mock
    private AsynchronousServerSocketChannel serverSocketChannel;
    @Mock
    private AsynchronousSocketChannel channel;
    @Mock
    private Scheduler scheduler;

    private ServerConnectionChannelCompletionHandler handler = new ServerConnectionChannelCompletionHandler();
    private ServerConnectionChannelContext context;

    @BeforeEach
    void setUp() {
        context = new ServerConnectionChannelContext(serverSocketChannel, scheduler);
    }

    @Test
    void shouldCallAcceptAndRead() {
        handler.completed(channel, context);

        verify(serverSocketChannel).accept(eq(context), eq(handler));
        verify(channel).read(any(ByteBuffer[].class), anyInt(), anyInt(), anyLong(), eq(TimeUnit.SECONDS), any(WorkerContext.class), any(ReaderChannelCompletionHandler.class));
    }
}