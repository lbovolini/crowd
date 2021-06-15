package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.worker.WorkerContext;

public interface IOChannelHandler {

    void handle(WorkerContext workerContext);
}
