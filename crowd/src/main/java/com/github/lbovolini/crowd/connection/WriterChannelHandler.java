package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.buffer.ByteBufferPool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.lbovolini.crowd.configuration.Config.BUFFER_ARRAY_SIZE;

/**
 * Responsável pela escrita assíncrona e não bloqueante das mensagens através dos canais de comunicação.
 */
public class WriterChannelHandler implements CompletionHandler<Long, WriterChannelContext> {

    public void completed(Long result, WriterChannelContext context) {

        int length = 0;

        ReentrantLock writeLock = context.getWriteLock();
        writeLock.lock();

        ByteBuffer[] bufferArray = context.getWriterBufferArray();

        try {
            if (result < 0) {
                context.close();
                return;
            }
            else if (result != 0) {
                ByteBufferPool writerPool = context.getWriterBufferPool();
                Queue<ByteBuffer> bufferQueue = context.getWriterBufferQueue();

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

                bufferArray = new ByteBuffer[Math.min(bufferQueue.size(), BUFFER_ARRAY_SIZE)];
                for (ByteBuffer buffer : bufferQueue) {
                    bufferArray[length] = buffer;
                    length++;

                    if (length >= bufferArray.length) {
                        break;
                    }
                }
                context.setWriterBufferArray(bufferArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }

        context.getChannel().write(bufferArray, 0, length, 0, TimeUnit.SECONDS, context, this);
    }

    public void failed(Throwable exc, WriterChannelContext context) {

        ReentrantLock writeLock = context.getWriteLock();
        writeLock.lock();

        try {
            context.close();
            context.getWriterBufferQueue().clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }
}
