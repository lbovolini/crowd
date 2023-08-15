package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.buffer.ByteBufferPool;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.PartialMessage;
import com.github.lbovolini.crowd.core.message.Flags;
import com.github.lbovolini.crowd.core.worker.WorkerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Responsável pela leitura assíncrona e não bloqueante das mensagens através dos canais de comunicação.
 */
public class ReaderChannelCompletionHandler implements CompletionHandler<Long, WorkerContext> {

    private static final Logger log = LoggerFactory.getLogger(ReaderChannelCompletionHandler.class);

    static final int TYPE = 1;
    static final int SIZE = 2;
    static final int DATA = 3;

    public void completed(Long result, WorkerContext context) {

        int length = 0;
        ReaderChannelContext readerContext = context.getReaderChannelContext();

        ReentrantLock readLock = readerContext.getReadLock();
        readLock.lock();

        ByteBuffer[] readerBufferArray = readerContext.getReaderBufferArray();

        try {
            if (result < 0) {
                readerContext.close();
                return;
            }
            else if (result != 0) {
                ByteBufferPool readerPool = ReaderChannelContext.getReaderBufferPool();
                Queue<ByteBuffer> readerBufferQueue = readerContext.getReaderBufferQueue();

                int i = 0;
                while (i < readerBufferArray.length) {
                    if (readerBufferArray[i] == null) {
                        break;
                    }

                    ByteBuffer byteBuffer = readerBufferQueue.peek();
                    byteBuffer.flip();

                    handle(context, byteBuffer, readerContext.getPartialMessage());

                    readerBufferArray[i] = null;
                    readerPool.offer(readerBufferQueue.poll());
                    i++;
                }

                ///
                if (readerBufferQueue.isEmpty()) {
                    readerBufferQueue.add(readerPool.poll());
                }

                /**
                 * Pega os buffers que estão na fila e insere-os no array de buffers de leitura até preencer
                 * todo o array ou até pegar todos os buffers da fila.
                 */
                for (ByteBuffer buffer : readerBufferQueue) {
                    readerBufferArray[length] = buffer;
                    length++;

                    if (length >= readerBufferArray.length) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error while reading from asynchronous channel");
            throw new UncheckedIOException(e);
        } finally {
            readLock.unlock();
        }

        // !TODO
        readerContext.getChannel().read(readerBufferArray, 0, length, 0, TimeUnit.SECONDS, context, this);
    }

    public void failed(Throwable exc, WorkerContext context) {

        ReaderChannelContext readerContext = context.getReaderChannelContext();
        ReentrantLock readLock = readerContext.getReadLock();
        readLock.lock();

        try {
            readerContext.getChannel().close();
            readerContext.getReaderBufferQueue().clear();
        } catch (IOException e) {
            log.error("Error while closing asynchronous channel");
        } finally {
            readLock.unlock();
        }
    }


    public static void handle(WorkerContext context, ByteBuffer buffer, PartialMessage partialMessage) {

        int position = partialMessage.getPosition();

        while (buffer.hasRemaining()) {

            switch (position) {

                case TYPE:
                    MessageBufferUtils.readTypeFromBuffer(buffer, partialMessage);
                    position = SIZE;
                    partialMessage.setPosition(SIZE);
                    break;

                case SIZE:
                    if (MessageBufferUtils.readSizeFromBuffer(buffer, partialMessage)) {
                        position = DATA;
                        partialMessage.setPosition(DATA);
                    }
                    break;

                case DATA:
                    if (MessageBufferUtils.readDataFromBuffer(buffer, partialMessage)) {
                        Flags flags = partialMessage.getFlags();
                        flags.resetAll();
                        Message message = Message.create(partialMessage.getType(), partialMessage.getData());

                        // !TODO
                        context.getMessageHandler().handle(message);

                        partialMessage.setPosition(TYPE);
                        position = TYPE;
                    }
                    break;
            }
        }
    }
}