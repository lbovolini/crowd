package com.github.lbovolini.crowd.group;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetSocketAddress;

import static com.github.lbovolini.crowd.configuration.Config.HEARTBEAT;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionTest {

    @Mock
    private MulticastWorker multicastWorker;

    @InjectMocks
    private Connection connection;

    @Test
    void shouldSendMessageToServer() {
        // Input
        String messageType = HEARTBEAT;

        // Should test ONLY this method
        connection.send(messageType);

        // Assertions
        verify(multicastWorker, only()).send(messageType);
    }

    @Test
    void shouldSendMessageToAllHosts() {
        // Input
        String messageType = HEARTBEAT;

        // Should test ONLY this method
        connection.sendAll(messageType);

        // Assertions
        verify(multicastWorker, only()).sendAll(messageType);
    }

    @Test
    void shouldSendMessageToSpecificHost() {
        // Input
        String messageType = HEARTBEAT;
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 0);

        // Should test ONLY this method
        connection.sendToHost(messageType, address);

        // Assertions
        verify(multicastWorker, only()).sendToHost(messageType, address);
    }
}