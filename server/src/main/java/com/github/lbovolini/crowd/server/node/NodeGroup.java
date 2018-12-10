package com.github.lbovolini.crowd.server.node;

import com.github.lbovolini.crowd.common.host.HostDetails;
import com.github.lbovolini.crowd.common.message.response.Response;
import com.github.lbovolini.crowd.common.connection.Connection;
import com.github.lbovolini.crowd.server.RemoteObject;
import com.github.lbovolini.crowd.server.Server;
import com.github.lbovolini.crowd.server.utils.ExecutionTimeUtils;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class NodeGroup {

    private final Server server;

    private final NodeService nodeService;
    private final Map<String, Node> ready;
    private final Map<String, Node> running;

    private final String className;
    private BiConsumer biConsumer;

    public NodeGroup(String className) {
        this.className = className;
        nodeService = new NodeService(this);
        ready = new ConcurrentHashMap<>();
        running = new ConcurrentHashMap<>();

        server = new Server(nodeService);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void join(HostDetails hostDetails, Connection connection) {
        Node node = new Node(hostDetails, connection);
        running.put(hostDetails.getId(), node);

        CompletableFuture.runAsync(() -> onReady(node));
    }

    public void leave(String id) {
        running.remove(id);
        ready.remove(id);
    }


    public void reply(Response response) {

        try {

            CompletableFuture future = running.get(response.getFrom()).getRef().getFuture(response.getUuid());

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

    public void stopAll() {
        running.values().forEach(node -> node.stop());
    }

    public void onReady(Node node) {
        try {
            UUID uuid = UUID.randomUUID();
            int cores = node.cores();
            ExecutionTimeUtils.start(uuid, cores);

            Object worker = RemoteObject.newInstance(className, node);

            for (int i = 0; i < cores; i++) {
                biConsumer.accept(worker, uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forAll(BiConsumer<Object, UUID> biConsumer) {
        this.biConsumer = biConsumer;
    }

    public void doWhile(BiConsumer<Object, UUID> biConsumer, Predicate predicate) {
        this.biConsumer = biConsumer;
    }

}
