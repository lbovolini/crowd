package com.github.lbovolini.crowd.scheduler;

public interface RequestHandler {
    void handle(MessageFrom messageFrom) throws Exception;

    void stop();
}
