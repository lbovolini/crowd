package com.github.lbovolini.crowd.connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.github.lbovolini.crowd.configuration.Config.BUFFER_SIZE;
import static com.github.lbovolini.crowd.configuration.Config.HEADER_SIZE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BufferUtilsTest {

    @Mock
    private static ByteBufferPool writerByteBufferPool;

    @Test
    void shouldPutRawMessageWithSingleBuffer() {

        // Input
        byte type = 1;
        byte[] data = new byte[] {};

        // Expected output
        ByteBuffer[] expectedOutput = new ByteBuffer[1];
        expectedOutput[0] = ByteBuffer.allocateDirect(BUFFER_SIZE);
        expectedOutput[0].put(type).putShort((short) data.length).put(data);
        expectedOutput[0].flip();

        // Mock behaviors
        when(writerByteBufferPool.poll()).thenAnswer(invocationOnMock -> ByteBuffer.allocateDirect(BUFFER_SIZE));

        // Should test ONLY this method
        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(type, data, writerByteBufferPool);

        // Assertions
        assertTrue(Arrays.equals(expectedOutput, byteBufferArray));
    }

    @Test
    void shouldPutRawMessageWithMultipleBuffers() {

        // Input
        byte type = 1;
        byte[] data = ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut " +
                "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse " +
                "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
                "qui officia deserunt mollit anim id est laborum.")
                .getBytes();

        // Expected output
        ByteBuffer[] expectedOutput = new ByteBuffer[(int) Math.ceil((data.length + HEADER_SIZE) / (double) BUFFER_SIZE)];

        for (int i = 0; i < expectedOutput.length; i++) {
            expectedOutput[i] = ByteBuffer.allocateDirect(BUFFER_SIZE);
        }

        expectedOutput[0].put(type).putShort((short) data.length);

        int writtenSum = Math.min(data.length, BUFFER_SIZE - HEADER_SIZE);

        expectedOutput[0].put(data, 0, writtenSum);
        expectedOutput[0].flip();

        for (int i = 1; i < expectedOutput.length; i++) {
            int newWritten = Math.min(data.length - writtenSum, BUFFER_SIZE);
            expectedOutput[i].put(data, writtenSum, newWritten);
            expectedOutput[i].flip();
            writtenSum += newWritten;
        }


        // Mock behaviors
        when(writerByteBufferPool.poll()).thenAnswer(invocationOnMock -> ByteBuffer.allocateDirect(BUFFER_SIZE));

        // Should test ONLY this method
        ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(type, data, writerByteBufferPool);

        // Assertions
        assertTrue(Arrays.equals(expectedOutput, byteBufferArray));
    }

    @Test
    void shouldThrowExceptionWhenPutRawMessageWithNullData() {

        // Input
        byte type = 1;
        byte[] data = null;

        // Assertions
        assertThrows(NullPointerException.class, () -> {
            // Should test ONLY this method
            ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(type, data, writerByteBufferPool);
        });
    }

    @Test
    void shouldThrowExceptionWhenPutRawMessageWithNullBufferPool() {

        // Input
        byte type = 1;
        byte[] data = new byte[] {};

        // Assertions
        assertThrows(NullPointerException.class, () -> {
            // Should test ONLY this method
            ByteBuffer[] byteBufferArray = BufferUtils.putRawMessage(type, data, null);
        });
    }
}