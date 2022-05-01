package com.github.lbovolini.crowd.discovery.request;

import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.message.ServerResponse;
import com.github.lbovolini.crowd.discovery.service.CodebaseService;

public class MulticastClientDispatcher implements MulticastDispatcher {

    private final CodebaseService codebaseService;

    public MulticastClientDispatcher(CodebaseService codebaseService) {
        this.codebaseService = codebaseService;
    }

    /**
     * Manipula somente mensagens recebidas do servidor.
     * Toda mensagem maior que 1 é uma mensagem enviada pelo servidor.
     * @param request
     */
    @Override
    public void dispatch(MulticastRequest request) {

        if (request.getMessage().getDataLength() <= 1) {
            return;
        }

        var response = ServerResponse.fromObject(request.getMessage().getDataAsString());

        var type = response.getType();
        var codebase = response.getCodebase();
        var libURL = response.getLibURL();

        switch (MulticastMessageType.get(type)) {
            case CONNECT -> codebaseService.onConnect(codebase, libURL, response.getServerAddress());
            case UPDATE -> codebaseService.onUpdate(codebase, libURL);
            case RELOAD -> codebaseService.onReload(codebase, libURL);
        }
    }

}
