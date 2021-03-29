package com.github.lbovolini.crowd.server;

import com.github.lbovolini.crowd.core.node.NodeGroup;
import com.github.lbovolini.crowd.server.worker.MulticastServerWorker;
import com.github.lbovolini.crowd.server.worker.MulticastServerWorkerFactory;
import com.github.lbovolini.crowd.server.worker.ServerWorker;
import com.github.lbovolini.crowd.server.worker.ServerWorkerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Crowd<T> {

    private final ServerWorker serverWorker;
    private final MulticastServerWorker multicastServer;
    private final ExecutorService serverMulticastThread;
    private final NodeGroup<T> nodeGroup;

    public Crowd(String className) throws IOException {
        this.nodeGroup = new NodeGroup<>(className);
        this.serverWorker = ServerWorkerFactory.defaultWorker(nodeGroup);
        this.multicastServer = MulticastServerWorkerFactory.defaultWorker();
        this.serverMulticastThread = Executors.newSingleThreadExecutor();
    }

    private void start() throws IOException {
        serverWorker.start();
        serverMulticastThread.execute(multicastServer);
    }

    public void forOne(Consumer<T> consumer) throws IOException {
        nodeGroup.setOperation(consumer);
        nodeGroup.setParallelism(1);
        start();
    }

    public void forAll(Consumer<T> consumer) throws IOException {
        nodeGroup.setOperation(consumer);
        start();
    }

}
