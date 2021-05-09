package com.github.lbovolini.crowd.discovery.test;

import com.github.lbovolini.crowd.discovery.message.ClientResponseFactory;
import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.message.ResponseFactory;
import com.github.lbovolini.crowd.discovery.message.ServerResponseFactory;
import com.github.lbovolini.crowd.discovery.request.MulticastClientRequestHandler;
import com.github.lbovolini.crowd.discovery.request.MulticastRequest;
import com.github.lbovolini.crowd.discovery.service.CodebaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.lbovolini.crowd.discovery.message.MulticastMessageType.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MulticastClientRequestHandlerTest {

    @Mock
    private CodebaseService codebaseService;

    @InjectMocks
    private MulticastClientRequestHandler requestHandler;

    private final ResponseFactory responseFactory = new ServerResponseFactory("localhost", 8080);

    @Test
    void shouldHandleConnectRequest() {
        // Input
        byte[] data = responseFactory.get(CONNECT);
        MulticastRequest request = new MulticastRequest(null, new MulticastMessage(data, data.length, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onConnect(any(), any(), any());
    }

    @Test
    void shouldHandleUpdateRequest() {
        // Input
        byte[] data = responseFactory.get(UPDATE);
        MulticastRequest request = new MulticastRequest(null, new MulticastMessage(data, data.length, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onUpdate(any(), any());
    }

    @Test
    void shouldHandleReloadRequest() {
        // Input
        byte[] data = responseFactory.get(RELOAD);
        MulticastRequest request = new MulticastRequest(null, new MulticastMessage(data, data.length, null));

        // Should test ONLY this method
        requestHandler.handle(request);

        verify(codebaseService, only()).onReload(any(), any());
    }
}