package com.github.lbovolini.crowd.connection;

import java.nio.ByteBuffer;
import java.util.Objects;

import static com.github.lbovolini.crowd.configuration.Config.*;
import static com.github.lbovolini.crowd.configuration.Config.BUFFER_SIZE;

public class BufferUtils {

    public static ByteBuffer[] putRawMessage(byte type, byte[] data, ByteBufferPool writerBufferPool) {

        Objects.requireNonNull(data);
        Objects.requireNonNull(writerBufferPool);

        int arrayLength = arrayLength(data.length);

        ByteBuffer[] byteBufferArray = initialize(arrayLength, writerBufferPool);

        putHeader(type, (short) data.length, byteBufferArray);
        putData(data, byteBufferArray);

        return byteBufferArray;
    }

    private static ByteBuffer[] initialize(int size, ByteBufferPool byteBufferPool) {

        ByteBuffer[] byteBufferArray = new ByteBuffer[size];

        int i = 0;
        while (i < size) {
            byteBufferArray[i] = byteBufferPool.poll();
            i++;
        }

        return byteBufferArray;
    }

    private static int arrayLength(int dataLength) {

        int size = HEADER_SIZE + dataLength;
        int arrayLength = (int) Math.ceil(size / (double) BUFFER_SIZE);

        if (arrayLength > BUFFER_ARRAY_SIZE || (arrayLength * BUFFER_SIZE) > MAX_MESSAGE_SIZE) {
            throw new RuntimeException("Buffers are greater than MAX_MESSAGE_SIZE");
        }

        return arrayLength;
    }

    private static int putHeader(byte type, short dataLength, ByteBuffer[] byteBufferArray) {

        int writtenSum = HEADER_SIZE;

        byteBufferArray[0]
                .put(type)
                .putShort(dataLength);

        return writtenSum;
    }

    private static int putData(byte[] data, ByteBuffer[] byteBufferArray) {

        int writtenSum = Math.min(data.length, BUFFER_SIZE - HEADER_SIZE);

        byteBufferArray[0].put(data, 0, writtenSum);
        byteBufferArray[0].flip();

        for (int i = 1; i < byteBufferArray.length; i++) {
            int newWritten = Math.min(data.length - writtenSum, BUFFER_SIZE);

            byteBufferArray[i].put(data, writtenSum, newWritten);
            byteBufferArray[i].flip();

            writtenSum += newWritten;
        }

        return writtenSum;
    }
}
