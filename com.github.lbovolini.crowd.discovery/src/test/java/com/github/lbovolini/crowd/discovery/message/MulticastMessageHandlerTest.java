package com.github.lbovolini.crowd.discovery.message;

import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.request.MulticastRequest;
import com.github.lbovolini.crowd.discovery.request.MulticastScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MulticastMessageHandlerTest {

    @Mock
    private MulticastMessage message;

    @Mock
    private MulticastConnection connection;
    @Mock
    private MulticastScheduler scheduler;

    @InjectMocks
    private MulticastMessageHandler messageHandler;

    @Test
    void shouldHandleMessage() {
        // Should test ONLY this method
        messageHandler.handle(message);

        // Assertions
        verify(scheduler, only()).enqueue(new MulticastRequest(connection, message));
    }
}