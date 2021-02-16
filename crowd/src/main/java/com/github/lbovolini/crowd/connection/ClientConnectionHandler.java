package com.github.lbovolini.crowd.connection;

import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.MessageFactory;

import java.io.IOException;
import java.nio.channels.CompletionHandler;

/**
 * Está classe é utilizada para lidar com o resultado da operação assíncrona de tentativa de conexão do socket local com o host remoto.
 * O resultado da tentativa será ou uma conexão realizada com sucesso, ou uma falha na conexão caso esta não possa ser estabelecida.
 * Assim, o método correto será invocado de acordo com este resultado.
 * Quando uma operação assíncrona de I/O completar ou falhar este handler será invocado para consumir o seu resultado.
 * Os métodos desta classe não devem, nunca, bloquear ou executar por um período de tempo que não seja mínimo.
 */
public class ClientConnectionHandler implements CompletionHandler<Void, ClientAttachment> {

    /**
     * Este método é invocado quando a operação assíncrona de I/O completar com sucesso.
     * Para permitir ao thread que invocou este handler possa atender a outros handlers, este método não deve, nunca,
     * bloquear ou executar por um período de tempo que não seja mínimo.
     * @param aVoid Resultado da operação assíncrona de I/O.
     * @param attachment Representa o contexto da atual operação assíncrona de I/O.
     * É o objeto associado à operação assíncrona de I/O quando esta foi iniciada.
     */
    public void completed(Void aVoid, ClientAttachment attachment) {

        Message message = null;
        try {
            message = MessageFactory.join(attachment.getCores());
        } catch (IOException e) { e.printStackTrace(); }


        Connection connection = new Connection(attachment.getChannel(), attachment.getScheduler());
        connection.send(message);
        connection.receive();
    }

    /**
     * Este método é invocado quando a operação assíncrona de I/O falhar.
     * Para permitir ao thread que invocou este handler possa atender a outros handlers, este método não deve, nunca,
     * bloquear ou executar por um período de tempo que não seja mínimo.
     * @param throwable Exceção que indica o motivo da falha da operação assíncrona de I/O.
     * @param attachment Representa o contexto da atual operação assíncrona de I/O.
     * É o objeto associado à operação assíncrona de I/O quando esta foi iniciada.
     */
    public void failed(Throwable throwable, ClientAttachment attachment) {
        throwable.printStackTrace();
    }
}
