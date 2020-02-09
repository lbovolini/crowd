package com.github.lbovolini.crowd.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class WriteHandler implements CompletionHandler<Long, IOChannel> {

    public void completed(Long result, IOChannel ioChannel) {

        int length = 0;

        ReentrantLock writeLock = ioChannel.getWriteLock();
        writeLock.lock();

        ByteBuffer[] bufferArray = ioChannel.getWriterBufferArray();

        try {
            if (result < 0) {
                ioChannel.close();
                return;
            }
            else if (result != 0) {
                ByteBufferPool writerPool = ioChannel.getWriterBufferPool();
                Queue<ByteBuffer> bufferQueue = ioChannel.getWriterBufferQueue();

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

        ioChannel.getChannel().write(bufferArray, 0, length, 0, TimeUnit.SECONDS, ioChannel, this);
    }

    public void failed(Throwable exc, IOChannel ioChannel) {

        ReentrantLock writeLock = ioChannel.getWriteLock();
        writeLock.lock();

        try {
            ioChannel.close();
            ioChannel.getWriterBufferQueue().clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }
}
