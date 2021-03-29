package com.github.lbovolini.crowd.discovery;

import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.connection.MulticastReaderChannel;
import com.github.lbovolini.crowd.discovery.connection.MulticastWriterChannel;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetSocketAddress;

import static com.github.lbovolini.crowd.discovery.message.MulticastMessageType.HEARTBEAT;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MulticastConnectionTest {

    @Mock
    private MulticastReaderChannel readerChannel;
    @Mock
    private MulticastWriterChannel writerChannel;

    @InjectMocks
    private MulticastConnection connection;

    @Test
    void shouldSendMessageToServer() {
        // Input
        MulticastMessageType messageType = HEARTBEAT;

        // Should test ONLY this method
        connection.send(messageType);

        // Assertions
        verify(writerChannel, only()).write(messageType.getType());
    }

    @Test
    void shouldSendMessageToAllHosts() {
        // Input
        MulticastMessageType messageType = HEARTBEAT;

        // Should test ONLY this method
        connection.multicastSend(messageType);

        // Assertions
        verify(writerChannel, only()).writeGroup(messageType.getType());
    }

    @Test
    void shouldSendMessageToSpecificHost() {
        // Input
        MulticastMessageType messageType = HEARTBEAT;
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 0);

        // Should test ONLY this method
        connection.send(messageType, address);

        // Assertions
        verify(writerChannel, only()).write(messageType.getType(), address);
    }

    @Test
    void shouldReceiveMessage() {
        // Should test ONLY this method
        connection.receive();

        // Assertions
        verify(readerChannel, only()).read();
    }
}