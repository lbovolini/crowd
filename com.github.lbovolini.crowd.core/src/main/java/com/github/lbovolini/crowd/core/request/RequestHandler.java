package com.github.lbovolini.crowd.core.request;

public interface RequestHandler {

    void handle(Request request) throws Exception;

    void stop();
}

