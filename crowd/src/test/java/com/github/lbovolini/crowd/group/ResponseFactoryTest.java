package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.group.message.MulticastMessageType;
import com.github.lbovolini.crowd.group.message.ResponseFactory;
import com.github.lbovolini.crowd.utils.CodebaseUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static com.github.lbovolini.crowd.configuration.Config.*;
import static com.github.lbovolini.crowd.group.message.MulticastMessageType.*;
import static org.junit.jupiter.api.Assertions.*;

class ResponseFactoryTest {

    @Test
    void shouldCreateHeartbeatMessage() {
        // Input
        MulticastMessageType messageType = HEARTBEAT;

        // Expected output
        byte[] expectedOutput = new byte[] { messageType.getType() };

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

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
        byte[] data = ResponseFactory.get(messageType);

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
                + HOST_NAME + SEPARATOR
                + PORT + SEPARATOR
                + libURL + SEPARATOR
                + messageType.getType()).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

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
                + HOST_NAME + SEPARATOR
                + PORT + SEPARATOR
                + libURL + SEPARATOR
                + messageType.getType()).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

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
                + HOST_NAME + SEPARATOR
                + PORT + SEPARATOR
                + libURL + SEPARATOR
                + messageType.getType()).getBytes(StandardCharsets.UTF_8);

        // Should test ONLY this method
        byte[] data = ResponseFactory.get(messageType);

        // Assertions
        assertArrayEquals(expectedOutput, data);
    }

}