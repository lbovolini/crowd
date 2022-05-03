package com.github.lbovolini.crowd.core.node;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.message.messages.Response;
import com.github.lbovolini.crowd.core.object.RemoteObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Grupo de nós. Contem todos os dispositivos (nós) candidatos e/ou participantes da computação.
 * @param <T>
 */
public class NodeGroup<T> {

    private static final Logger log = LoggerFactory.getLogger(NodeGroup.class);

    private final String className;
    private Consumer<T> consumer;

    private final Map<Long, Node> running;

    private int parallelism;

    public NodeGroup(String className) {
        this.className = className;
        this.running = new ConcurrentHashMap<>();
    }

    /**
     * Permite a adicão de um dispositivo remoto ao grupo de nós.
     * @param cores Número de núcleos de processamento do dispositivo disponíveis para a computação.
     * @param connection
     */
    public void join(int cores, Connection connection) {
        Node node = new Node(cores, connection);
        running.put(connection.getHostId(), node);
        onJoin(node);
    }

    /**
     * Permite a remoção de um dispositivo remoto do grupo de nós.
     * @param id
     */
    public void leave(long id) {
        running.remove(id);
    }

    public void reply(Response response, Connection connection) {

        try {
            CompletableFuture<? super Object> future = running.get(connection.getHostId()).getRemoteObject().getFuture(response.getRequestId());

            if (response.getException() != null) {
                future.completeExceptionally(response.getException());
                return;
            }
            future.complete(response.getResult());
        } catch (Exception e) {
            log.error("Error while setting method invocation response", e);
        }
    }


    public void setOperation(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    @SuppressWarnings("unchecked")
    private void onJoin(Node node) {
        try {
            T worker = (T) RemoteObject.newInstance(className, node);
            int cores = parallelism != 0 ? parallelism : node.cores();

            for (int i = 0; i < cores; i++) {
                consumer.accept(worker);
            }
        } catch (Exception e) {
            log.error("Error while creating remote object", e);
        }
    }

}
