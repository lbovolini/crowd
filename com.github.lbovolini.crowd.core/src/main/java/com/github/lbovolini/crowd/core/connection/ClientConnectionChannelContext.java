package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.request.Scheduler;

import java.nio.channels.AsynchronousSocketChannel;

/**
 * Contém as informações do cliente necessárias para realizar a comunicação com o servidor.
 * Representa o contexto da atual operação assíncrona de I/O.
 */
public class ClientConnectionChannelContext {

    private final AsynchronousSocketChannel channel;
    private final Scheduler scheduler;
    private final Integer cores;

    public ClientConnectionChannelContext(AsynchronousSocketChannel channel, Scheduler scheduler, int cores) {
        this.channel = channel;
        this.scheduler = scheduler;
        this.cores = cores;
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Integer getCores() {
        return cores;
    }
}
