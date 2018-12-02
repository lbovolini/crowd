package com.github.lbovolini.crowd.common.handler;

import com.github.lbovolini.crowd.common.message.Message;
import com.github.lbovolini.crowd.common.message.PartialMessage;
import com.github.lbovolini.crowd.common.message.Flags;
import com.github.lbovolini.crowd.common.connection.ByteBufferPool;
import com.github.lbovolini.crowd.common.connection.Connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.lbovolini.crowd.common.configuration.Config.BUFFER_SIZE;
import static com.github.lbovolini.crowd.common.configuration.Config.HEADER_SIZE;
import static com.github.lbovolini.crowd.common.configuration.Config.MAX_MESSAGE_SIZE;

public class ReadHandler implements CompletionHandler<Long, Connection> {

    static final int TYPE = 1;
    static final int SIZE = 2;
    static final int DATA = 3;

    public void completed(Long result, Connection connection) {

        int length = 0;

        ReentrantLock readLock = connection.getReadLock();
        readLock.lock();

        ByteBuffer[] readerBufferArray = connection.getReaderBufferArray();

        try {
            if (result < 0) {
                connection.close();
                return;
            }
            else if (result != 0) {
                ByteBufferPool readerPool = connection.getReaderBufferPool();
                Queue<ByteBuffer> readerBufferQueue = connection.getReaderBufferQueue();

                int i = 0;
                while (i < readerBufferArray.length) {
                    if (readerBufferArray[i] == null) {
                        break;
                    }

                    ByteBuffer byteBuffer = readerBufferQueue.peek();
                    byteBuffer.flip();

                    handle(connection, byteBuffer, connection.getPartialMessage());

                    readerBufferArray[i] = null;
                    readerPool.offer(readerBufferQueue.poll());
                    i++;
                }

                ///
                if (readerBufferQueue.isEmpty()) {
                    readerBufferQueue.add(readerPool.pool());
                }

                for (ByteBuffer buffer : readerBufferQueue) {
                    readerBufferArray[length] = buffer;
                    length++;

                    if (length >= readerBufferArray.length) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }

        connection.getChannel().read(readerBufferArray, 0, length, 0, TimeUnit.SECONDS, connection, this );
    }

    public void failed(Throwable exc, Connection connection) {

        ReentrantLock readLock = connection.getReadLock();
        readLock.lock();

        try {
            connection.close();
            connection.getReaderBufferQueue().clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
    }


    public void handle(Connection connection, ByteBuffer buffer, PartialMessage partialMessage) {

        int position = partialMessage.getPosition();

        while (buffer.hasRemaining()) {

            switch (position) {

                case TYPE:
                    readTypeFromBuffer(buffer, partialMessage);
                    position = SIZE;
                    partialMessage.setPosition(SIZE);
                    break;

                case SIZE:
                    if (readSizeFromBuffer(buffer, partialMessage)) {
                        position = DATA;
                        partialMessage.setPosition(DATA);
                    }
                    break;

                case DATA:
                    if (readDataFromBuffer(buffer, partialMessage)) {
                        Message message = Message.create(partialMessage.getType(), partialMessage.getData());
                        connection.handle(message);
                        partialMessage.setPosition(TYPE);
                        position = TYPE;
                    }
                    break;
            }
        }
    }

    public void readTypeFromBuffer(ByteBuffer buffer, PartialMessage partialMessage) {
        partialMessage.setType(buffer.get());
    }

    public boolean readSizeFromBuffer(ByteBuffer byteBuffer, PartialMessage partialMessage) {

        Flags flags = partialMessage.getFlags();

            if (!flags.hasSizeChunk()) {
                flags.setHasSizeChunk(true);
                if (byteBuffer.remaining() < Short.BYTES) {
                    partialMessage.setSizeFirstByte(byteBuffer.get());
                } else {
                    short size = byteBuffer.getShort();
                    partialMessage.setSize(size);
                    return true;
                }
            } else {
                partialMessage.setSizeLastByte(byteBuffer.get());
                return true;
            }
            return false;
    }

    public boolean readDataFromBuffer(ByteBuffer buffer, PartialMessage partialMessage) {

        Flags flags = partialMessage.getFlags();

        if (!flags.hasMessageChunk()) {
            // !todo
            if (partialMessage.getSize() > (MAX_MESSAGE_SIZE - HEADER_SIZE)) {
                System.out.println("Message size limit exceeded");
            }
            partialMessage.allocate();

            int read = Math.min(buffer.remaining(), partialMessage.getSize());
            partialMessage.setReadSize(read);
            partialMessage.read(buffer, 0, read);
            flags.setHasMessageChunk(true);

            if (read == partialMessage.getSize()) {
                flags.setHasMessage(true);
                flags.resetAll();
                return true;
            }
        } else {

            int remaining = partialMessage.getSize() - partialMessage.getReadSize();

            if (remaining > buffer.remaining()) {
                int read = partialMessage.getReadSize();

                int inBufferSize = buffer.remaining();
                partialMessage.read(buffer, partialMessage.getReadSize(), inBufferSize);
                partialMessage.setReadSize(read + inBufferSize);

                return false;
            }

            partialMessage.read(buffer, partialMessage.getReadSize(), remaining);
            flags.setHasMessage(true);
            flags.resetAll();
            return true;
        }

        return false;
    }
}