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

/**
 * Representa o Stub para o objeto remoto. Permite instanciar o objeto no dispositivo remoto, assim como realizar a invocação de seus métodos.
 */
public class RemoteObject implements InvocationHandler {

    private final Node node;

    private final AtomicInteger requestIdCounter;
    private final Map<Integer, CompletableFuture<? super Object>> requests;

    private RemoteObject(Node node) {
        this.node = node;
        this.requestIdCounter = new AtomicInteger(0);
        this.requests = new ConcurrentHashMap<>();
    }

    /**
     * Instancia um objeto remoto no dispositivo informado utilizando o construtor padrão.
     * @param className
     * @param node
     * @return
     * @throws Exception
     */
    public static Object newInstance(String className, Node node) throws Exception {
        return newInstance(className, null, null, node);
    }

    /**
     * Instancia um objeto remoto no dispositivo informado utilizando um construtor parametrizado.
     * @param className
     * @param args
     * @param parameterTypes
     * @param node
     * @return
     * @throws Exception
     */
    public static Object newInstance(String className, Object[] args, Class<?>[] parameterTypes, Node node) throws Exception {

        RemoteObject remoteObject = new RemoteObject(node);
        remoteObject.create(className, parameterTypes, args);

        return Proxy.newProxyInstance(
                Class.forName(className).getClassLoader(),
                Class.forName(className).getInterfaces(),
                remoteObject);
    }

    /**
     * Envia a mensagem de criação de objeto para o dispositivo remoto.
     * @param className
     * @param parameterTypes
     * @param args
     * @throws IOException
     */
    private void create(String className, Class<?>[] parameterTypes, Object[] args) throws IOException {
        Message message = MessageFactory.create(className, parameterTypes, args);
        this.node.setRemoteObject(this);
        this.node.send(message);
    }


    /**
     * Permite a invocação remota dos métodos do objeto remoto.
     * @param o
     * @param method
     * @param args
     * @return
     * @throws Exception
     */
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Exception {

        boolean isPrimitiveVoid = Void.TYPE.equals(method.getReturnType());
        int requestId = 0;

        if (!isPrimitiveVoid) { requestId = getRequestId(); }

        Message message = MessageFactory.invoke(requestId, method.getName(), method.getParameterTypes(), args);
        this.node.send(message);

        if (isPrimitiveVoid) { return null; }

        CompletableFuture<? super Object> future = new CompletableFuture<>();
        this.requests.put(requestId, future);
        return future;
    }

    private int getRequestId() {
        return requestIdCounter.incrementAndGet();
    }

    public CompletableFuture<? super Object> getFuture(int requestId) {
        return requests.remove(requestId);
    }

}
