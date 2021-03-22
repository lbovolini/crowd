package com.github.lbovolini.crowd.group;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.lbovolini.crowd.configuration.Config.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MulticastWorkerClientRequestHandlerTest {

    @Mock
    private CodebaseService codebaseService;

    @InjectMocks
    private MulticastClientRequestHandler requestHandler;

    @Test
    void shouldHandleConnectRequest() {
        // Input
        Request request = new Request(null, MulticastMessage.ofType(CONNECT, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onConnect(any(), any(), any());
    }

    @Test
    void shouldHandleUpdateRequest() {
        // Input
        Request request = new Request(null, MulticastMessage.ofType(UPDATE, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onUpdate(any(), any());
    }

    @Test
    void shouldHandleReloadRequest() {
        // Input
        Request request = new Request(null, MulticastMessage.ofType(RELOAD, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onReload(any(), any());
    }
}