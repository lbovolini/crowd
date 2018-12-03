package com.github.lbovolini.crowd.server;

import com.github.lbovolini.crowd.common.message.request.*;
import com.github.lbovolini.crowd.common.object.Service;
import com.github.lbovolini.crowd.server.node.Node;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.*;

public class RemoteObject implements Service, InvocationHandler {

    private final Node node;
    private ConcurrentHashMap<UUID, CompletableFuture> methodResponse = new ConcurrentHashMap<>();

    private RemoteObject(Node node) {
        this.node = node;
    }

    private void create(String className, Object[] args) throws Exception {
        Request request = new CreateObject(className, args);
        send(request);
    }

    public static Object newInstance(String className, Node node) throws Exception {
        return newInstance(className, null, node);
    }

    public static Object newInstance(String className, Object[] args, Node node) throws Exception {
        RemoteObject self = new RemoteObject(node);
        self.create(className, args);
        node.setRef(self);
        return java.lang.reflect.Proxy.newProxyInstance(
                Class.forName(className).getClassLoader(), Class.forName(className).getInterfaces(), self);
    }

    private void send(Request request) throws Exception {
        node.invoke(request);
    }

    public Object invoke(Object o, Method method, Object[] args) throws Exception {

        Request request;
        final CompletableFuture result = new CompletableFuture<>();
        boolean service = RequestType.isService(method);

        if (service) {
            request = new ServiceRequest(method.getName());
        } else {
            UUID uuid = UUID.randomUUID();
            request = new InvokeMethod(uuid, method.getName(), args);
            methodResponse.put(uuid, result);
            result.thenRun(() -> methodResponse.remove(uuid));
        }

        send(request);

        return service ? null : result;
    }

    public void stop() {
        try {
            Method method = Service.class.getDeclaredMethod("stop", null);
            invoke(null, method, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closed() {
        ConcurrentHashMap<UUID, CompletableFuture> map = methodResponse;
        methodResponse = null;
        map.forEach(((uuid, completableFuture) -> completableFuture.completeExceptionally(new RemoteException())));

    }

    public CompletableFuture getFuture(UUID uuid) {
        return methodResponse.remove(uuid);
    }
}
