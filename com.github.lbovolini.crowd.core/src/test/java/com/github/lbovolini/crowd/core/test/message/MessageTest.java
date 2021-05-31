package com.github.lbovolini.crowd.core.test.message;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private final byte type = MessageType.JOIN.getType();
    private final byte[] data = new byte[] { 1 };
    private final int size = Message.HEADER_SIZE + data.length;

    private final Message message = Message.create(type, data);

    @Test
    void shouldCreateMessageWithByteAsType() {
        // Should test ONLY this method
        Message message = Message.create(type, data);

        // Assertions
        assertNotNull(message);
        assertEquals(message.getType(), type);
        assertArrayEquals(message.getData(), data);
        assertEquals(message.getSize(), size);
        assertEquals(message.getDataLength(), data.length);
    }

    @Test
    void shouldCreateMessageWithMessageTypeAsType() {
        // Should test ONLY this method
        Message message = Message.create(MessageType.JOIN, data);

        // Assertions
        assertNotNull(message);
        assertEquals(message.getType(), type);
        assertArrayEquals(message.getData(), data);
        assertEquals(message.getSize(), size);
        assertEquals(message.getDataLength(), data.length);
    }

    @Test
    void shouldCreateMessageWithMessageTypeAsTypeAndObjectAsData() {
        try {
            // Input
            MyObject myObject = new MyObject(10);
            byte[] data = Message.serialize(myObject);

            // Should test ONLY this method
            Message message = Message.create(MessageType.JOIN, myObject);

            // Assertions
            assertNotNull(message);
            assertEquals(message.getType(), type);
            assertArrayEquals(message.getData(), data);
            assertEquals(message.getSize(), data.length + Message.HEADER_SIZE);
            assertEquals(message.getDataLength(), data.length);

        } catch (IOException e) {
            e.printStackTrace();
            fail(e);
        }
    }

    @Test
    void shouldReturnMessageType() {
        // Should test ONLY this method
        int messageType = message.getType();

        // Assertion
        assertEquals(type, messageType);
    }

    @Test
    void shouldReturnMessageSize() {
        // Should test ONLY this method
        int messageSize = message.getSize();

        // Assertion
        assertEquals(size, messageSize);
    }

    @Test
    void shouldReturnMessageData() {
        // Should test ONLY this method
        byte[] messageData = message.getData();

        // Assertion
        assertArrayEquals(data, messageData);
    }

    @Test
    void shouldReturnMessageDataLength() {
        // Should test ONLY this method
        short messageDataLength = message.getDataLength();

        // Assertion
        assertEquals(data.length, messageDataLength);
    }
}