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

        // Should test ONLY this method
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

        // Should test ONLY this method
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

        // Should test ONLY this method
        var hasMessage = MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);

        assertEquals(Integer.BYTES, partialMessage.getSize());
        assertEquals(Integer.BYTES, partialMessage.getReadSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertEquals(1, partialMessage.getPosition());
        assertArrayEquals(ByteBuffer.allocate(4).putInt(Integer.MAX_VALUE).array(), partialMessage.getData());
        assertTrue(hasMessage);
    }

    /**
     * Partial size read, just one byte
     */
    @Test
    void shouldReadSizeFromBufferWhenBufferRemainingEqualsOne() {
        var expectedFlags = new Flags();
        expectedFlags.setHasType(true);
        expectedFlags.setHasSizeChunk(true);
        expectedFlags.setHasSize(false);

        var buffer = ByteBuffer.allocate(100);
        buffer.put((byte) 127);
        buffer.put((byte) 1);
        buffer.flip();
        var partialMessage = new PartialMessage();

        MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);

        // Should test ONLY this method
        var hasSize = MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage);

        assertEquals(0, partialMessage.getSize());
        assertNull(partialMessage.getData());
        assertEquals(0, partialMessage.getReadSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertEquals(1, partialMessage.getPosition());
        assertFalse(hasSize);
    }

    /**
     * read size byte by byte
     */
    @Test
    void shouldReadSizeFromBufferWhenFlagsHaveSizeChunk() {
        var expectedFlags = new Flags();
        expectedFlags.setHasType(true);
        expectedFlags.setHasSizeChunk(true);
        expectedFlags.setHasSize(true);

        var buffer = ByteBuffer.allocate(100);
        buffer.put((byte) 127);
        buffer.put((byte) 1);
        buffer.flip();
        var partialMessage = new PartialMessage();

        MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);

        MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage);

        buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 2);
        buffer.flip();

        // Should test ONLY this method
        var hasSize = MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage);

        assertEquals(258, partialMessage.getSize());
        assertNull(partialMessage.getData());
        assertEquals(0, partialMessage.getReadSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertEquals(1, partialMessage.getPosition());
        assertTrue(hasSize);
    }

    /**
     * Partial data read, just one byte
     */
    @Test
    void shouldReadDataFromBufferMessageChunk() {
        var expectedFlags = new Flags();
        expectedFlags.setHasType(true);
        expectedFlags.setHasSizeChunk(true);
        expectedFlags.setHasSize(true);
        expectedFlags.setHasMessageChunk(true);
        expectedFlags.setHasMessage(false);

        var buffer = ByteBuffer.allocate(100);
        buffer.put((byte) 127);
        buffer.putShort((byte) Integer.BYTES);
        // data
        buffer.put((byte) 2);
        buffer.flip();
        var partialMessage = new PartialMessage();

        MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);
        MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage);

        // Should test ONLY this method
        var hasData = MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);

        assertEquals(Integer.BYTES, partialMessage.getSize());
        assertEquals(1, partialMessage.getReadSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertFalse(hasData);
    }

    /**
     * read one byte then read remaining
     */
    @Test
    void shouldReadDataFromBufferWhenHasMessageChunk() {
        var expectedFlags = new Flags();
        expectedFlags.setHasType(true);
        expectedFlags.setHasSizeChunk(true);
        expectedFlags.setHasSize(true);
        expectedFlags.setHasMessageChunk(true);
        expectedFlags.setHasMessage(true);

        var buffer = ByteBuffer.allocate(100);
        buffer.put((byte) 127);
        buffer.putShort((byte) Integer.BYTES);
        // data
        buffer.put((byte) 2);
        buffer.flip();
        var partialMessage = new PartialMessage();

        MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);
        MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage);

        MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);
        buffer = ByteBuffer.allocate(3);
        buffer.put((byte) 7);
        buffer.put((byte) 4);
        buffer.put((byte) 5);
        buffer.flip();

        // Should test ONLY this method
        var hasData = MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);

        assertEquals(Integer.BYTES, partialMessage.getSize());
        assertEquals(Integer.BYTES, partialMessage.getReadSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertTrue(hasData);
    }

    /**
     * read data byte by byte
     */
    @Test
    void shouldReadDataFromBufferByteByByte() {
        var expectedFlags = new Flags();
        expectedFlags.setHasType(true);
        expectedFlags.setHasSizeChunk(true);
        expectedFlags.setHasSize(true);
        expectedFlags.setHasMessageChunk(true);
        expectedFlags.setHasMessage(true);

        var buffer = ByteBuffer.allocate(100);
        buffer.put((byte) 127);
        buffer.putShort((byte) Integer.BYTES);
        // data
        buffer.put((byte) 2);
        buffer.flip();
        var partialMessage = new PartialMessage();

        MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);
        MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage);

        MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);
        buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 7);
        buffer.flip();

        MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);
        MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);
        buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 4);
        buffer.flip();

        MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);

        MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);
        buffer = ByteBuffer.allocate(1);
        buffer.put((byte) 5);
        buffer.flip();

        // Should test ONLY this method
        var hasData = MessageBufferUtils.readDataFromBuffer(buffer, partialMessage);

        assertEquals(Integer.BYTES, partialMessage.getSize());
        assertEquals(Integer.BYTES, partialMessage.getReadSize());
        assertEquals(expectedFlags, partialMessage.getFlags());
        assertTrue(hasData);
    }
}