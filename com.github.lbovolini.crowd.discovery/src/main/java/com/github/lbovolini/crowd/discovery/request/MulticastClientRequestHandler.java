package com.github.lbovolini.crowd.discovery.request;

import com.github.lbovolini.crowd.discovery.service.CodebaseService;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.message.ServerResponse;

import java.net.URL;

public class MulticastClientRequestHandler implements MulticastRequestHandler {

    private final CodebaseService codebaseService;

    public MulticastClientRequestHandler(CodebaseService codebaseService) {
        this.codebaseService = codebaseService;
    }

    /**
     * Manipula somente mensagens recebidas do servidor.
     * Toda mensagem maior que 1 é uma mensagem enviada pelo servidor.
     * @param request
     */
    @Override
    public void handle(MulticastRequest request) {

        if (request.getMessage().getDataLength() <= 1) {
            return;
        }

        ServerResponse response = ServerResponse.fromObject(request.getMessage().getDataAsString());

        byte type = Byte.parseByte(response.getType());
        URL[] codebase = response.getCodebase();
        URL libURL = response.getLibURL();

        switch (MulticastMessageType.get(type)) {
            case CONNECT:
                codebaseService.onConnect(codebase, libURL, response.getServerAddress());
                break;
            case UPDATE:
                codebaseService.onUpdate(codebase, libURL);
                break;
            case RELOAD:
                codebaseService.onReload(codebase, libURL);
                break;
        }
    }

}
