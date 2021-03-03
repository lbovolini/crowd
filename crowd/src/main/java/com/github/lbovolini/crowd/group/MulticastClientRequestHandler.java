package com.github.lbovolini.crowd.group;

import java.net.URL;

import static com.github.lbovolini.crowd.configuration.Config.*;

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
    public void handle(Request request) {

        if (request.getMessage().getDataLength() <= 1) {
            return;
        }

        ServerResponse response = ServerResponse.fromObject(request.getMessage().getDataAsString());

        String type = response.getType();
        URL[] codebase = response.getCodebase();
        URL libURL = response.getLibURL();

        switch (type) {
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
