package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.connection.ReaderChannel;
import com.github.lbovolini.crowd.core.connection.WriterChannel;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.request.RequestQueue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import static com.github.lbovolini.crowd.core.message.MessageType.HEARTBEAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionTest {

    @Mock
    private AsynchronousSocketChannel channel;
    @Mock
    private ReaderChannel readerChannel;
    @Mock
    private WriterChannel writerChannel;
    @Mock
    private RequestQueue requestQueue;

    @InjectMocks
    private Connection connection;

    @Test
    void shouldReturnHostId() throws IOException {
        // Expected output
        long expectedOutput = 1270018080L;

        // Mock behavior
        when(channel.getRemoteAddress()).thenReturn(new InetSocketAddress("localhost",8080));

        // Should test ONLY this method
        long hostId = connection.getHostId();

        // Assertions
        assertEquals(expectedOutput, hostId);
    }

    @Test
    void shouldReturnRemoteAddress() throws IOException {
        // Expected output
        String expectedOutput = "127.0.0.1";

        // Mock behavior
        when(channel.getRemoteAddress()).thenReturn(new InetSocketAddress("localhost",8080));

        // Should test ONLY this method
        String remoteAddress = connection.getRemoteAddress();

        // Assertions
        assertEquals(expectedOutput, remoteAddress);
    }

    @Test
    void shouldReturnRemotePort() throws IOException {
        // Expected output
        int expectedOutput = 8080;

        // Mock behavior
        when(channel.getRemoteAddress()).thenReturn(new InetSocketAddress("localhost",8080));

        // Should test ONLY this method
        int remotePort = connection.getRemotePort();

        // Assertions
        assertEquals(expectedOutput, remotePort);
    }

    @Test
    void shouldReturnChannel() {
        // Should test ONLY this method
        AsynchronousSocketChannel socketChannel = connection.getChannel();

        // Assertions
        assertEquals(channel, socketChannel);
    }

    @Test
    void shouldSendMessage() {
        // Input
        byte[] data = new byte[] {};
        Message message = Message.create(HEARTBEAT, data);

        // Mock behavior
        when(writerChannel.write(message.getType(), message.getData())).thenReturn(false);

        // Should test ONLY this method
        connection.send(message);

        // Assertions
        verify(writerChannel).write(message.getType(), message.getData());
    }

    @Test
    void shouldReceiveMessage() {

        // Mock behavior
        when(readerChannel.read()).thenReturn(false);

        // Should test ONLY this method
        connection.receive();

        // Assertions
        verify(readerChannel).read();
    }

    @Test
    void shouldCloseConnection() throws IOException {

        // Should test ONLY this method
        connection.close();

        // Assertions
        verify(readerChannel).close();
        verify(writerChannel).close();
    }

}