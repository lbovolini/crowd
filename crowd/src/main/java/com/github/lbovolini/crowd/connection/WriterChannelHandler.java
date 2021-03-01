package com.github.lbovolini.crowd.connection;

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

    public void completed(Long result, WriterChannelContext channelContext) {

        int length = 0;

        ReentrantLock writeLock = channelContext.getWriteLock();
        writeLock.lock();

        ByteBuffer[] bufferArray = channelContext.getWriterBufferArray();

        try {
            if (result < 0) {
                channelContext.close();
                return;
            }
            else if (result != 0) {
                ByteBufferPool writerPool = channelContext.getWriterBufferPool();
                Queue<ByteBuffer> bufferQueue = channelContext.getWriterBufferQueue();

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
                channelContext.setWriterBufferArray(bufferArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }

        channelContext.getChannel().write(bufferArray, 0, length, 0, TimeUnit.SECONDS, channelContext, this);
    }

    public void failed(Throwable exc, WriterChannelContext channelContext) {

        ReentrantLock writeLock = channelContext.getWriteLock();
        writeLock.lock();

        try {
            channelContext.close();
            channelContext.getWriterBufferQueue().clear();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }
}
