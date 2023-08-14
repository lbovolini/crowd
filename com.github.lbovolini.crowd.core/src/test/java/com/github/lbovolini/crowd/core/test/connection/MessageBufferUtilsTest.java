package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.connection.MessageBufferUtils;
import com.github.lbovolini.crowd.core.message.Flags;
import com.github.lbovolini.crowd.core.message.PartialMessage;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class MessageBufferUtilsTest {

    @Test
    void shouldReadTypeFromBufferAndPutIntoPartialMessage() {

        var expectedFlags = new Flags();
        expectedFlags.setHasType(true);

        var buffer = ByteBuffer.allocate(100);
        buffer.put((byte) 127);
        buffer.flip();
        var partialMessage = new PartialMessage();

        MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);

        assertEquals((byte) 127, partialMessage.getType());
        assertNull(partialMessage.getData());
        assertEquals(0, partialMessage.getReadSize());
        assertEquals(0, partialMessage.getSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertEquals(1, partialMessage.getPosition());
    }

    @Test
    void shouldReadSizeFromBufferAndPutIntoPartialMessage() {

        var expectedFlags = new Flags();
        expectedFlags.setHasType(true);
        expectedFlags.setHasSizeChunk(true);
        expectedFlags.setHasSize(true);

        var buffer = ByteBuffer.allocate(100);
        buffer.put((byte) 127);
        buffer.putShort((short) 256);
        buffer.flip();
        var partialMessage = new PartialMessage();

        MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);
        var hasSize = MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage);

        assertEquals(256, partialMessage.getSize());
        assertNull(partialMessage.getData());
        assertEquals(0, partialMessage.getReadSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertEquals(1, partialMessage.getPosition());
        assertTrue(hasSize);
    }

    @Test
    void shouldReadDataFromBufferAndPutIntoPartialMessage() {
        var expectedFlags = new Flags();
        expectedFlags.setHasType(true);
        expectedFlags.setHasSizeChunk(true);
        expectedFlags.setHasSize(true);
        expectedFlags.setHasMessageChunk(true);
        expectedFlags.setHasMessage(true);
        expectedFlags.setHasSize(true);

        var buffer = ByteBuffer.allocate(100);
        buffer.put((byte) 127);
        buffer.putShort((short) Integer.BYTES);
        buffer.putInt(Integer.MAX_VALUE);
        buffer.flip();
        var partialMessage = new PartialMessage();

        MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);
        MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage);
        var hasMessage = MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);

        assertEquals(Integer.BYTES, partialMessage.getSize());
        assertEquals(Integer.BYTES, partialMessage.getReadSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertEquals(1, partialMessage.getPosition());
        assertArrayEquals(ByteBuffer.allocate(4).putInt(Integer.MAX_VALUE).array(), partialMessage.getData());
        assertTrue(hasMessage);
    }
}