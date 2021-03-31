package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageFactory;
import com.github.lbovolini.crowd.core.request.RequestQueue;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Está classe é utilizada para lidar com o resultado da operação assíncrona de tentativa de conexão do socket local com o host remoto.
 * O resultado da tentativa será ou uma conexão realizada com sucesso, ou uma falha na conexão caso esta não possa ser estabelecida.
 * Assim, o método correto será invocado de acordo com este resultado.
 * Quando uma operação assíncrona de I/O completar ou falhar este handler será invocado para consumir o seu resultado.
 * Os métodos desta classe não devem, nunca, bloquear ou executar por um período de tempo que não seja mínimo.
 */
public class ClientConnectionChannelCompletionHandler implements CompletionHandler<Void, ClientConnectionChannelContext> {

    /**
     * Este método é invocado quando a operação assíncrona de I/O completar com sucesso.
     * Para permitir ao thread que invocou este handler possa atender a outros handlers, este método não deve, nunca,
     * bloquear ou executar por um período de tempo que não seja mínimo.
     * @param aVoid Resultado da operação assíncrona de I/O.
     * @param context Representa o contexto da atual operação assíncrona de I/O.
     * É o objeto associado à operação assíncrona de I/O quando esta foi iniciada.
     */
    public void completed(Void aVoid, ClientConnectionChannelContext context) {

        Message message = MessageFactory.join(context.getCores());

        AsynchronousSocketChannel channel = context.getChannel();

        ReaderChannelContext readerChannelContext = new ReaderChannelContext(channel);
        WriterChannelContext writerChannelContext = new WriterChannelContext(channel);

        ReaderChannel readerChannel = new ReaderChannel(readerChannelContext);
        WriterChannel writerChannel = new WriterChannel(writerChannelContext);

        RequestQueue requestQueue = context.getScheduler();
        Connection connection = new Connection(channel, readerChannel, writerChannel, requestQueue);

        connection.send(message);
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
    public void failed(Throwable throwable, ClientConnectionChannelContext context) {
        throwable.printStackTrace();
    }
}
