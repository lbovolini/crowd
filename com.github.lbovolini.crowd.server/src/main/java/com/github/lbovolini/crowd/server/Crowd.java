package com.github.lbovolini.crowd.server;

import com.github.lbovolini.crowd.core.node.NodeGroup;
import com.github.lbovolini.crowd.core.util.HostUtils;
import com.github.lbovolini.crowd.server.worker.MulticastServerWorker;
import com.github.lbovolini.crowd.server.worker.MulticastServerWorkerFactory;
import com.github.lbovolini.crowd.server.worker.ServerWorker;
import com.github.lbovolini.crowd.server.worker.ServerWorkerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Crowd<T> {

    public static final String HOSTNAME = System.getProperty("hostname", HostUtils.getHostAddressName());
    public static final int PORT = Integer.parseInt(System.getProperty("port", String.valueOf(8081)));

    private final ServerWorker serverWorker;
    private final MulticastServerWorker multicastServer;
    private final ExecutorService serverMulticastThread;
    private final NodeGroup<T> nodeGroup;

    public Crowd(String className) throws IOException {
        this.nodeGroup = new NodeGroup<>(className);
        this.serverWorker = ServerWorkerFactory.defaultWorker(nodeGroup, new InetSocketAddress(HOSTNAME, PORT));
        this.multicastServer = MulticastServerWorkerFactory.defaultWorker(HOSTNAME, PORT);
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
