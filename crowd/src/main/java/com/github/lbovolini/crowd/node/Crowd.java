package com.github.lbovolini.crowd.node;

import com.github.lbovolini.crowd.group.MulticastServer;
import com.github.lbovolini.crowd.group.MulticastServerRequestHandler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Crowd<T> {

    private final Server server;
    private final MulticastServer multicastServer;
    private final ExecutorService serverMulticastThread;
    private final NodeGroup<T> nodeGroup;

    public Crowd(String className) throws IOException {
        this.nodeGroup = new NodeGroup<>(className);
        this.server = new Server(nodeGroup);
        this.multicastServer = new MulticastServer(new MulticastServerRequestHandler());
        this.serverMulticastThread = Executors.newSingleThreadExecutor();
    }

    private void start() throws IOException {
        server.start();
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
