package com.github.lbovolini.crowd.scheduler;

import com.github.lbovolini.crowd.classloader.Context;

public interface RequestHandler {

    void handle(MessageFrom messageFrom) throws Exception;

    void stop();

    Context getContext();
}

