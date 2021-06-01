package com.github.lbovolini.crowd.discovery.test.message;

import com.github.lbovolini.crowd.discovery.exception.InvalidMulticastMessageException;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.message.ResponseFactory;
import com.github.lbovolini.crowd.discovery.message.ServerResponseFactory;
import com.github.lbovolini.crowd.discovery.util.CodebaseUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static com.github.lbovolini.crowd.discovery.message.MulticastMessageType.*;
import static com.github.lbovolini.crowd.discovery.message.ResponseFactory.SEPARATOR;
import static org.junit.jupiter.api.Assertions.*;

class ServerResponseFactoryTest {

    private final String hostname = "localhost";
    private final int port = 8080;

    private final ResponseFactory responseFactory = new ServerResponseFactory(hostname, port);

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
    void shouldCreateUpdateMessage() {
        // Input
        MulticastMessageType messageType = UPDATE;

        // Expected output
        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        byte[] expectedOutput = (codebase + SEPARATOR
                + hostname + SEPARATOR
                + port + SEPARATOR
                + libURL + SEPARATOR
                + messageType.getType()).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = responseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldCreateConnectMessage() {
        // Input
        MulticastMessageType messageType = CONNECT;

        // Expected output
        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        byte[] expectedOutput = (codebase + SEPARATOR
                + hostname + SEPARATOR
                + port + SEPARATOR
                + libURL + SEPARATOR
                + messageType.getType()).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = responseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldCreateReloadMessage() {
        // Input
        MulticastMessageType messageType = RELOAD;

        // Expected output
        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        byte[] expectedOutput = (codebase + SEPARATOR
                + hostname + SEPARATOR
                + port + SEPARATOR
                + libURL + SEPARATOR
                + messageType.getType()).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = responseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldThrowsInvalidMulticastMessageException() {
        // Input
        MulticastMessageType messageType = DISCOVER;

        // Assertions
        assertThrows(InvalidMulticastMessageException.class, () -> {
            // Should test ONLY this method
            responseFactory.get(messageType);
        });
    }
}