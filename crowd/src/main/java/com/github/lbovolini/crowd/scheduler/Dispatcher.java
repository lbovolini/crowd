package com.github.lbovolini.crowd.scheduler;


public class Dispatcher {

    private final RequestHandler handler;

    public Dispatcher(RequestHandler handler) {
        this.handler = handler;
    }

    public void dispatch(MessageFrom messageFrom) throws Exception {
        handler.handle(messageFrom);
    }

    public RequestHandler getHandler() {
        return handler;
    }
}
