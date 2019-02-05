package com.github.lbovolini.crowd.node;

import com.github.lbovolini.crowd.Server;
import com.github.lbovolini.crowd.connection.Connection;
import com.github.lbovolini.crowd.group.ServerMulticaster;
import com.github.lbovolini.crowd.message.messages.Response;
import com.github.lbovolini.crowd.object.RemoteObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NodeGroup<T> {

    private final String className;
    private Consumer<T> consumer;

    private final Server server;
    private final ServerMulticaster multicaster;
    private final ExecutorService pool;

    private final Map<Long, Node> running;

    private boolean useAllCores = true;

    public NodeGroup(String className) throws IOException {
        this.className = className;
        this.running = new ConcurrentHashMap<>();
        this.server = new Server(this);
        this.multicaster = new ServerMulticaster();
        this.pool = Executors.newSingleThreadExecutor();

        start();
    }

    private void start() throws IOException {
        server.start();
        pool.execute(multicaster::start);
    }

    public void join(int cores, Connection connection) {
        Node node = new Node(cores, connection);
        running.put(connection.getHostId(), node);
        onReady(node);
    }

    public void leave(String id) {
        running.remove(id);
    }

    public void reply(Response response, Connection connection) {

        try {
            CompletableFuture future = running.get(connection.getHostId()).getRemoteObject().getFuture(response.getRequestId());

            if (response.getException() != null) {
                future.completeExceptionally(response.getException());
                return;
            }
            future.complete(response.getResult());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public Node getNode(String id) {
        return running.get(id);
    }

    public void onReady(Node node) {
        try {
            T worker = (T) RemoteObject.newInstance(className, node);
            int cores = 1;

            if (useAllCores) {
                cores = node.cores();
            }

            for (int i = 0; i < cores; i++) {
                consumer.accept(worker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forOne(Consumer<T> consumer) {
        this.consumer = consumer;
        this.useAllCores = false;
    }

    public void forAll(Consumer<T> consumer) {
        this.consumer = consumer;
        this.useAllCores = true;
    }



}
