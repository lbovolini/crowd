package com.github.lbovolini.crowd.client.test.worker;

import com.github.lbovolini.crowd.client.worker.ClientWorker;
import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelCompletionHandler;
import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientWorkerTest {

    @Mock
    private AsynchronousSocketChannel channel;
    @Mock
    private ClientConnectionChannelContext context;

    @InjectMocks
    private ClientWorker clientWorker;

    @Test
    void shouldConnect() {
        var address = new InetSocketAddress(0);

        clientWorker.connect(address);

        verify(channel).connect(eq(address), eq(context), any(ClientConnectionChannelCompletionHandler.class));
    }

    @Test
    void shouldCloseConnection() {
        try {
            clientWorker.close();

            verify(channel).close();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}