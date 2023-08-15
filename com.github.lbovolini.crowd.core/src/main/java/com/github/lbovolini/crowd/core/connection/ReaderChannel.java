package com.github.lbovolini.crowd.core.connection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;

public class ReaderChannel {

    private final ReaderChannelContext context;

    public ReaderChannel(ReaderChannelContext context) {
        this.context = context;
    }

    public ReaderChannelContext getContext() {
        return context;
    }

    /**
     * Cria buffers de leitura e adiciona-os à fila de leitura.
     * Se a fila de leitura não está vazia então o processamento da leitura está em progresso
     * e não é necessario dar início ao processamento novamente.
     * Se a fila de leitura está vazia então o processamento não está em progresso e é necessario iniciá-lo.
     * @return true se é necessário dar início ao processamento da leitura.
     */
    public boolean read() {

        ByteBuffer[] byteBufferArray = new ByteBuffer[1];
        byteBufferArray[0] = ReaderChannelContext.getReaderBufferPool().poll();

        Lock readLock = context.getReadLock();
        readLock.lock();

        try {
            if (isClosed()) { return false; }

            final boolean wasEmpty = context.getReaderBufferQueue().isEmpty();
            context.getReaderBufferQueue().addAll(Arrays.asList(byteBufferArray));

            if (!wasEmpty) { return false; }
        }
        finally {
            readLock.unlock();
        }

        context.setReaderBufferArray(byteBufferArray);
        return true;
    }

    public boolean isClosed() {
        return context.isClosed();
    }

    public void close() throws IOException {
        context.close();
    }
}
