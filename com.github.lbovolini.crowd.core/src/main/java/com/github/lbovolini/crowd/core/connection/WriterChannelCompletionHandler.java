package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.buffer.ByteBufferPool;
import com.github.lbovolini.crowd.core.worker.WorkerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.lbovolini.crowd.core.buffer.BufferUtils.BUFFER_ARRAY_SIZE;

/**
 * Responsável pela escrita assíncrona e não bloqueante das mensagens através dos canais de comunicação.
 */
public class WriterChannelCompletionHandler implements CompletionHandler<Long, WorkerContext> {

    private static final Logger log = LoggerFactory.getLogger(WriterChannelCompletionHandler.class);

    public void completed(Long result, WorkerContext context) {

        int length = 0;

        WriterChannelContext writerContext = context.getWriterChannelContext();
        ReentrantLock writeLock = writerContext.getWriteLock();
        writeLock.lock();

        ByteBuffer[] bufferArray = writerContext.getWriterBufferArray();

        try {
            if (result < 0) {
                writerContext.close();
                return;
            }
            else if (result != 0) {
                ByteBufferPool writerPool = WriterChannelContext.getWriterBufferPool();
                Queue<ByteBuffer> bufferQueue = writerContext.getWriterBufferQueue();

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
                writerContext.setWriterBufferArray(bufferArray);
            }
        } catch (IOException e) {
            log.error("Error while writing to asynchronous channel");
        } finally {
            writeLock.unlock();
        }

        context.requestWrite();
    }

    public void failed(Throwable exc, WorkerContext context) {

        WriterChannelContext writerContext = context.getWriterChannelContext();
        ReentrantLock writeLock = writerContext.getWriteLock();
        writeLock.lock();

        try {
            writerContext.close();
            writerContext.getWriterBufferQueue().clear();
        } catch (IOException e) {
            log.error("Error while closing asynchronous channel");
        } finally {
            writeLock.unlock();
        }
    }
}
