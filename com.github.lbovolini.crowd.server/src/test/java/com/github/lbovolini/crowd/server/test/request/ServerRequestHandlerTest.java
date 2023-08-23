package com.github.lbovolini.crowd.server.test.request;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageType;
import com.github.lbovolini.crowd.core.message.messages.JoinGroup;
import com.github.lbovolini.crowd.core.message.messages.Response;
import com.github.lbovolini.crowd.core.node.NodeGroup;
import com.github.lbovolini.crowd.core.request.Request;
import com.github.lbovolini.crowd.server.request.ServerRequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServerRequestHandlerTest {

    @Mock
    private NodeGroup<?> nodeGroup;
    @Mock
    private Connection connection;
    private ServerRequestHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ServerRequestHandler(nodeGroup);
    }

    @Test
    void shouldHandleJoinRequest() throws Exception {

        var request = new Request(connection, Message.create(MessageType.JOIN, new JoinGroup(1)));

        handler.handle(request);

        verify(nodeGroup, only()).join(anyInt(), any(Connection.class));
    }

    @Test
    void shouldHandleReplyRequest() throws Exception {

        var request = new Request(connection, Message.create(MessageType.REPLY, new Response(0, "", null)));

        handler.handle(request);

        verify(nodeGroup, only()).reply(any(Response.class), any(Connection.class));
    }
}