package com.github.lbovolini.crowd.connection;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Está classe é utilizada para lidar com o resultado da operação assíncrona de requisição de conexão feita por um
 * socket de um cliente remoto para este socket servidor.
 * O resultado da operação, se a conexão for aceita, será a criação de um novo canal de socket assíncrono para a nova conexão,
 * ou uma falha na operação, caso a conexão não seja aceita.
 * Assim, o método correto será invocado de acordo com este resultado.
 * Quando uma operação assíncrona de I/O completar ou falhar este handler será invocado para consumir o seu resultado.
 * Os métodos desta classe não devem, nunca, bloquear ou executar por um período de tempo que não seja mínimo.
 */
public class ServerConnectionChannelHandler implements CompletionHandler<AsynchronousSocketChannel, ServerConnectionChannelContext> {


    /**
     * Este método é invocado quando a operação assíncrona de I/O completar com sucesso.
     * Para permitir ao thread que invocou este handler possa atender a outros handlers, este método não deve, nunca,
     * bloquear ou executar por um período de tempo que não seja mínimo.
     * @param channel Um socket assíncrono conectado ao cliente remoto.
     * @param context Representa o contexto da atual operação assíncrona de I/O.
     * É o objeto associado à operação assíncrona de I/O quando esta foi iniciada.
     */
    public void completed(AsynchronousSocketChannel channel, ServerConnectionChannelContext context) {

        context.getServerChannel().accept(context, this);

        ReaderChannelContext readerChannelContext = new ReaderChannelContext(channel);
        WriterChannelContext writerChannelContext = new WriterChannelContext(channel);

        ReaderChannel readerChannel = new ReaderChannel(readerChannelContext);
        WriterChannel writerChannel = new WriterChannel(writerChannelContext);

        Connection connection = new Connection(channel, readerChannel, writerChannel);

        MessageHandler messageHandler = new MessageHandler(context.getScheduler(), connection);
        readerChannelContext.setMessageHandler(messageHandler);

        connection.receive();
    }

    /**
     * Este método é invocado quando a operação assíncrona de I/O falhar.
     * Para permitir ao thread que invocou este handler possa atender a outros handlers, este método não deve, nunca,
     * bloquear ou executar por um período de tempo que não seja mínimo.
     * @param throwable Exceção que indica o motivo da falha da operação assíncrona de I/O.
     * @param context Representa o contexto da atual operação assíncrona de I/O.
     * É o objeto associado à operação assíncrona de I/O quando esta foi iniciada.
     */
    public void failed(Throwable throwable, ServerConnectionChannelContext context) {
        throwable.printStackTrace();
    }
}