package com.github.lbovolini.crowd.common.handler;

import com.github.lbovolini.crowd.common.connection.ByteBufferPool;
import com.github.lbovolini.crowd.common.connection.Connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Based on https://www.codeproject.com/Tips/766107/AsynchronousSocketChannel-Concurrent-Writes
 */
public class WriteHandler implements CompletionHandler<Long, Connection> {

    public void completed(Long result, Connection connection) {

        int length = 0;

        ReentrantLock writeLock = connection.getWriteLock();
        writeLock.lock();

        ByteBuffer[] bufferArray = connection.getWriterBufferArray();

        try {
            if (result < 0) {
                connection.close();
                return;
            }
            else if (result != 0) {
                ByteBufferPool writerPool = connection.getWriterBufferPool();
                Queue<ByteBuffer> bufferQueue = connection.getWriterBufferQueue();

                int i = 0;
                while (i < bufferArray.length) {
                    if (bufferArray[i] == null) {
                        break;
                    }

                    ByteBuffer byteBuffer = bufferQueue.peek();
                    if (byteBuffer.remaining() > 0) {
                        break;
                    }

                    bufferArray[i] = null;
                    writerPool.offer(bufferQueue.poll());
                    i++;
                }

                if (bufferQueue.isEmpty()) {
                    return;
                }

                for (ByteBuffer buffer : bufferQueue) {
                    bufferArray[length] = buffer;
                    length++;

                    if (length >= bufferArray.length) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }

        connection.getChannel().write(bufferArray, 0, length, 0, TimeUnit.SECONDS, connection, this);
    }

    public void failed(Throwable exc, Connection connection) {

        ReentrantLock writeLock = connection.getWriteLock();
        writeLock.lock();

        try {
            connection.close();
            connection.getWriterBufferQueue().clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }
}
