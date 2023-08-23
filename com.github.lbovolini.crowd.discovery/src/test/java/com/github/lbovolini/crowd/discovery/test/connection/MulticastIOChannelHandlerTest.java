package com.github.lbovolini.crowd.discovery.test.connection;

import com.github.lbovolini.crowd.discovery.connection.MulticastIOChannelHandler;
import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MulticastIOChannelHandlerTest {

    @Mock
    private DatagramChannel channel;

    @Test
    void shouldHandleReadWhenBufferIsNotReady() throws IOException {
        var messageReady = MulticastIOChannelHandler.read(channel);

        assertNull(messageReady);
    }

    @Test
    void shouldHandleReadWhenBufferIsReady() throws IOException {
        var expectedMessage = new MulticastMessage(new byte[]{MulticastMessageType.HEARTBEAT.getType()}, 1, new InetSocketAddress(8888));

        given(channel.receive(any(ByteBuffer.class))).willAnswer(e -> {
            ByteBuffer buffer = e.getArgument(0);
            buffer.put(MulticastMessageType.HEARTBEAT.getType());

            return new InetSocketAddress(8888);
        });

        var message = MulticastIOChannelHandler.read(channel);

        assertNotNull(message);

        assertEquals(expectedMessage, message);
    }

    @Test
    void shouldHandleWriteWhenNoBytesSent() throws IOException {

        var message = new MulticastMessage(new byte[]{MulticastMessageType.HEARTBEAT.getType()}, 1, new InetSocketAddress(8888));

        var isSent = MulticastIOChannelHandler.write(channel, message);

        assertFalse(isSent);
    }

    @Test
    void shouldHandleWriteWhenBytesSent() throws IOException {

        var message = new MulticastMessage(new byte[]{MulticastMessageType.HEARTBEAT.getType()}, 1, new InetSocketAddress(8888));

        given(channel.send(any(ByteBuffer.class), any(SocketAddress.class))).willAnswer(e -> 1);

        var isSent = MulticastIOChannelHandler.write(channel, message);

        assertTrue(isSent);
    }
}