package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.scheduler.Scheduler;

import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * Contém as informações do servidor necessárias para realizar a comunicação com os clientes.
 * Representa o contexto da atual operação assíncrona de I/O.
 */
public class ServerAttachment {

    private final AsynchronousServerSocketChannel serverChannel;
    private final Scheduler scheduler;

    public ServerAttachment(AsynchronousServerSocketChannel serverChannel, Scheduler scheduler) {
        this.serverChannel = serverChannel;
        this.scheduler = scheduler;
    }

    public AsynchronousServerSocketChannel getServerChannel() {
        return serverChannel;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
