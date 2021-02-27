package com.github.lbovolini.crowd.node;

import com.github.lbovolini.crowd.group.ServerMulticast;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Crowd<T> {

    private final Server server;
    private final ServerMulticast serverMulticast;
    private final ExecutorService serverMulticastThread;
    private final NodeGroup<T> nodeGroup;

    public Crowd(String className) throws IOException {
        this.nodeGroup = new NodeGroup<>(className);
        this.server = new Server(nodeGroup);
        this.serverMulticast = new ServerMulticast();
        this.serverMulticastThread = Executors.newSingleThreadExecutor();
    }

    private void start() throws IOException {
        server.start();
        serverMulticastThread.execute(serverMulticast);

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
