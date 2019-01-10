package com.github.lbovolini.crowd.object;

import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.MessageFactory;
import com.github.lbovolini.crowd.node.Node;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RemoteObject implements InvocationHandler {

    private final Node node;

    private final AtomicInteger requestIdCounter;
    private final Map<Integer, CompletableFuture> requests;

    private RemoteObject(Node node) {
        this.node = node;
        this.requestIdCounter = new AtomicInteger(0);
        this.requests = new ConcurrentHashMap<>();
    }

    public static Object newInstance(String className, Node node) throws Exception {
        return newInstance(className, null, node);
    }
    

    public static Object newInstance(String className, Object[] args, Node node) throws Exception {

        RemoteObject remoteObject = new RemoteObject(node);
        remoteObject.create(className, args);

        return Proxy.newProxyInstance(
                Class.forName(className).getClassLoader(),
                Class.forName(className).getInterfaces(),
                remoteObject);
    }

    private void create(String className, Object[] args) throws IOException {
        Message message = MessageFactory.create(className, args);
        this.node.setRemoteObject(this);
        this.node.send(message);
    }


    public Object invoke(Object o, Method method, Object[] args) throws Exception {
        int requestId = getRequestId();

        Message message = MessageFactory.invoke(requestId, method.getName(), args);
        this.node.send(message);

        CompletableFuture<?> future = new CompletableFuture<>();
        this.requests.put(requestId, future);
        return future;
    }

    private int getRequestId() {
        return requestIdCounter.incrementAndGet();
    }

    public CompletableFuture getFuture(int requestId) {
        return requests.remove(requestId);
    }


}
