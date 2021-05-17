package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.connection.MessageHandler;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageFactory;
import com.github.lbovolini.crowd.core.request.RequestQueue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageHandlerTest {

    @Mock
    private Connection connection;

    @Mock
    private RequestQueue requestQueue;

    @InjectMocks
    private MessageHandler messageHandler;

    @Test
    void shouldAddRequestToQueue() {
        // Input
        Message message = MessageFactory.join(1);
        // Should test ONLY this method
        messageHandler.handle(message);

        // Assertion
        verify(requestQueue, only()).enqueue(any());
    }
}