package com.github.lbovolini.crowd.scheduler;

import com.github.lbovolini.crowd.classloader.Context;

public interface RequestHandler {

    void handle(Request request) throws Exception;

    void stop();

    Context getContext();
}

