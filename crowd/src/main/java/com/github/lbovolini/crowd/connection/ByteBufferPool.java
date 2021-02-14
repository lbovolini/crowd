package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.configuration.Config;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Fila utilizada para otimizar a alocação de buffers.
 * Todos os métodos são thread-safe.
 */
public class ByteBufferPool {

    private final ConcurrentLinkedDeque<ByteBuffer> byteBufferDeque;

    public ByteBufferPool() {
        byteBufferDeque = new ConcurrentLinkedDeque<>();
    }

    /**
     * Remove um buffer da fila e retorna, ou, se a fila estiver vazia, cria e retorna um novo buffer.
     * @return Retorna um buffer de tamanho definido pela constante BUFFER_SIZE.
     */
    public ByteBuffer poll() {

        ByteBuffer buffer = byteBufferDeque.poll();

        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(Config.BUFFER_SIZE);
        }

        return buffer;
    }

    /**
     * Limpa e insere o buffer na fila.
     * @param byteBuffer Buffer
     */
    public void offer(ByteBuffer byteBuffer) {
        byteBuffer.clear();
        byteBufferDeque.offer(byteBuffer);
    }
}
