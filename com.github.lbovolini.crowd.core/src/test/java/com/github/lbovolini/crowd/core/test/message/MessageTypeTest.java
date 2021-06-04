package com.github.lbovolini.crowd.core.test.message;

import com.github.lbovolini.crowd.core.message.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTypeTest {

    @Test
    void shouldReturnJoinMessageAsOneByte() {
        // Expected output
        byte expectedOutput = (byte) 1;

        MessageType messageType = MessageType.JOIN;

        // Should test ONLY this method
        byte type = messageType.getType();

        // Assertions
        assertEquals(expectedOutput, type);
    }

    @Test
    void shouldReturnNullWhenByteIsNotMappedAsMessageType() {
        // Input
        byte input = (byte) 0;

        // Should test ONLY this method
        MessageType messageType = MessageType.get(input);

        // Assertions
        assertNull(messageType);
    }
}