package com.github.lbovolini.crowd.discovery.test.message;

import com.github.lbovolini.crowd.discovery.message.ClientResponseFactory;
import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class MulticastMessageTest {

    private final byte[] data = new ClientResponseFactory().get(MulticastMessageType.HEARTBEAT);
    private final InetSocketAddress address = new InetSocketAddress(8888);

    private final MulticastMessage multicastMessage = new MulticastMessage(data, data.length, address);

    @Test
    void shouldReturnMulticastMessageType() {
        // Expected output
        byte expectedOutput = MulticastMessageType.HEARTBEAT.getType();

        // Should test ONLY this method
        byte type = multicastMessage.getType();

        // Assertions
        assertEquals(expectedOutput, type);
    }

    @Test
    void shouldReturnMulticastMessageData() {
        // Should test ONLY this method
        byte[] data = multicastMessage.getData();

        // Assertions
        assertArrayEquals(this.data, data);
    }

    @Test
    void shouldReturnMulticastMessageDataAsString() {
        // Expected output
        String expectedOutput = new String(data, 0, data.length, StandardCharsets.UTF_8);

        // Should test ONLY this method
        String stringData = multicastMessage.getDataAsString();

        // Assertions
        assertEquals(expectedOutput, stringData);
    }

    @Test
    void shouldReturnRemoteHostAddress() {
        // Should test ONLY this method
        InetSocketAddress inetSocketAddress = multicastMessage.getAddress();

        // Assertion
        assertEquals(address, inetSocketAddress);
    }

    @Test
    void shouldReturnDataLength() {
        // Should test ONLY this method
        int length = multicastMessage.getDataLength();

        // Assertions
        assertEquals(data.length, length);
    }
}