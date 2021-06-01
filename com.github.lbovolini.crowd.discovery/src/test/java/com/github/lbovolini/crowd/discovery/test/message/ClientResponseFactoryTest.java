package com.github.lbovolini.crowd.discovery.test.message;

import com.github.lbovolini.crowd.discovery.exception.InvalidMulticastMessageException;
import com.github.lbovolini.crowd.discovery.message.ClientResponseFactory;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.message.ResponseFactory;
import org.junit.jupiter.api.Test;

import static com.github.lbovolini.crowd.discovery.message.MulticastMessageType.*;
import static org.junit.jupiter.api.Assertions.*;

class ClientResponseFactoryTest {

    private final ResponseFactory responseFactory = new ClientResponseFactory();

    @Test
    void shouldCreateHeartbeatMessage() {
        // Input
        MulticastMessageType messageType = HEARTBEAT;

        // Expected output
        byte[] expectedOutput = new byte[] { messageType.getType() };

        // Should test ONLY this method
        byte[] data = responseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldCreateDiscoverMessage() {
        // Input
        MulticastMessageType messageType = DISCOVER;

        // Expected output
        byte[] expectedOutput = new byte[] { messageType.getType() };

        // Should test ONLY this method
        byte[] data = responseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldThrowsInvalidMulticastMessageException() {
        // Input
        MulticastMessageType messageType = INVOKE;

        // Assertions
        assertThrows(InvalidMulticastMessageException.class, () -> {
            // Should test ONLY this method
            responseFactory.get(messageType);
        });
    }

}