package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.utils.CodebaseUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static com.github.lbovolini.crowd.configuration.Config.*;
import static org.junit.jupiter.api.Assertions.*;

class ResponseFactoryTest {

    @Test
    void shouldCreateHeartbeatMessage() {
        // Input
        String messageType = HEARTBEAT;

        // Expected output
        byte[] expectedOutput = messageType.getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldCreateDiscoverMessage() {
        // Input
        String messageType = DISCOVER;

        // Expected output
        byte[] expectedOutput = messageType.getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldCreateUpdateMessage() {
        // Input
        String messageType = UPDATE;

        // Expected output
        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        byte[] expectedOutput = (codebase + SEPARATOR
                + HOST_NAME + SEPARATOR
                + PORT + SEPARATOR
                + libURL + SEPARATOR
                + messageType).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldCreateConnectMessage() {
        // Input
        String messageType = CONNECT;

        // Expected output
        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        byte[] expectedOutput = (codebase + SEPARATOR
                + HOST_NAME + SEPARATOR
                + PORT + SEPARATOR
                + libURL + SEPARATOR
                + messageType).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldCreateReloadMessage() {
        // Input
        String messageType = RELOAD;

        // Expected output
        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        byte[] expectedOutput = (codebase + SEPARATOR
                + HOST_NAME + SEPARATOR
                + PORT + SEPARATOR
                + libURL + SEPARATOR
                + messageType).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

    @Test
    void shouldCreateEmptyMessageWhenUnknownMessageType() {
        // Input
        String messageType = "SOMETHING THAT IS NOT A MESSAGE TYPE";

        // Expected output
        byte[] expectedOutput = new byte[] {};

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }
}