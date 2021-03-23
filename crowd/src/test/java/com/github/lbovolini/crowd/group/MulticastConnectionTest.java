package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.group.connection.MulticastConnection;
import com.github.lbovolini.crowd.group.connection.MulticastReaderChannel;
import com.github.lbovolini.crowd.group.connection.MulticastWriterChannel;
import com.github.lbovolini.crowd.group.message.MulticastMessageType;
import com.github.lbovolini.crowd.group.worker.MulticastWorker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetSocketAddress;

import static com.github.lbovolini.crowd.group.message.MulticastMessageType.HEARTBEAT;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MulticastConnectionTest {

    @Mock
    private MulticastReaderChannel readerChannel;
    @Mock
    private MulticastWriterChannel writerChannel;

    @InjectMocks
    private MulticastConnection multicastConnection;

    @Test
    void shouldSendMessageToServer() {
        // Input
        MulticastMessageType messageType = HEARTBEAT;

        // Should test ONLY this method
        multicastConnection.send(messageType);

        // Assertions
        verify(writerChannel, only()).write(messageType.getType());
    }

    @Test
    void shouldSendMessageToAllHosts() {
        // Input
        MulticastMessageType messageType = HEARTBEAT;

        // Should test ONLY this method
        multicastConnection.multicastSend(messageType);

        // Assertions
        verify(writerChannel, only()).writeGroup(messageType.getType());
    }

    @Test
    void shouldSendMessageToSpecificHost() {
        // Input
        MulticastMessageType messageType = HEARTBEAT;
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 0);

        // Should test ONLY this method
        multicastConnection.send(messageType, address);

        // Assertions
        verify(writerChannel, only()).write(messageType.getType(), address);
    }
}