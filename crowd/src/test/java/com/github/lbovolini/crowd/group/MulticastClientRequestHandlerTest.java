package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.group.message.MulticastMessage;
import com.github.lbovolini.crowd.group.message.MulticastMessageType;
import com.github.lbovolini.crowd.group.request.MulticastClientRequestHandler;
import com.github.lbovolini.crowd.group.request.MulticastRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.lbovolini.crowd.group.message.MulticastMessageType.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MulticastClientRequestHandlerTest {

    @Mock
    private CodebaseService codebaseService;

    @InjectMocks
    private MulticastClientRequestHandler requestHandler;

    @Test
    void shouldHandleConnectRequest() {
        // Input
        MulticastRequest request = new MulticastRequest(null, MulticastMessage.ofType(CONNECT, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onConnect(any(), any(), any());
    }

    @Test
    void shouldHandleUpdateRequest() {
        // Input
        MulticastRequest request = new MulticastRequest(null, MulticastMessage.ofType(UPDATE, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onUpdate(any(), any());
    }

    @Test
    void shouldHandleReloadRequest() {
        // Input
        MulticastRequest request = new MulticastRequest(null, MulticastMessage.ofType(RELOAD, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onReload(any(), any());
    }
}