package com.github.lbovolini.crowd.client;

import com.github.lbovolini.crowd.common.message.Message;
import com.github.lbovolini.crowd.common.message.request.CreateObject;
import com.github.lbovolini.crowd.common.message.request.InvokeMethod;
import com.github.lbovolini.crowd.common.object.Proxy;
import com.github.lbovolini.crowd.common.object.Service;
import com.github.lbovolini.crowd.common.message.request.Request;
import com.github.lbovolini.crowd.common.message.request.ServiceRequest;
import com.github.lbovolini.crowd.common.classloader.RemoteClassLoader;
import com.github.lbovolini.crowd.common.connection.Connection;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.*;

public class Scheduler implements Proxy, Service {

    private Object object;
    private Connection connection;

    private final Thread thread;

    private static BlockingDeque<Message> requests;
    private static ExecutorService pool;

    private static URLClassLoader loader = null;
    private String codebase;

    public void setCodebase(String codebase) {
        this.codebase = codebase;
    }

    public Scheduler(String codebase) {
        setCodebase(codebase);
        requests = new LinkedBlockingDeque<>();
        if (loader == null)
            loader = new RemoteClassLoader(getCodeBase(), Thread.currentThread().getContextClassLoader());
        thread = new Thread(() -> dispatch());
        thread.setContextClassLoader(loader);
        onClose();
    }

    public void start(Connection connection) {
        this.connection = connection;

        if (!thread.isAlive()) {
            thread.start();
        }
    }

    private void dispatch() {
        while (true) {
            try {
                Message message = requests.take();
                Request request = (Request) Message.deserialize(message.getData());

                // InvokeMethod
                if (request instanceof InvokeMethod) {
                    pool.execute(() -> Servant.execute(object, request, connection));
                }
                // ServiceRequest
                else if (request instanceof ServiceRequest) {
                    Servant.execute(this, request, connection);
                }
                // CreateObject
                else if (request instanceof CreateObject) {
                    stop();
                    object = newInstance(request.getName(), request.getArgs());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void enqueue(Message message) throws InterruptedException {
        requests.put(message);
    }

    private Object newInstance(String className, Object[] args) throws Exception {
        Class classDefinition = loader.loadClass(className);
        Constructor constructor = classDefinition.getConstructor(getTypes(args));
        return constructor.newInstance(args);
    }

    private static Class<?>[] getTypes(Object[] args) {

        if (args == null || args.length == 0) {
            return null;
        }
        Class<?>[] types = new Class[args.length];

        for (int i = 0; i < types.length; i++) {
            types[i] = args[i].getClass();
        }
        return types;
    }

    public void stop() {
        try {
            if (pool != null) {
                pool.shutdownNow();
                pool.awaitTermination(1, TimeUnit.SECONDS);
            }
            pool = Executors.newFixedThreadPool(Client.getCores());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void kill() {

    }

    private URL[] getCodeBase() {
        if (codebase.equals("") || codebase == null) {
            return null;
        }
        String[] strURL = codebase.split(" ");
        URL[] urls = new URL[strURL.length];

        for (int i = 0; i < strURL.length; i++) {
            try {
                urls[i] = new URL(strURL[i]);
            } catch (MalformedURLException e) { e.printStackTrace(); }
        }

        return urls;
    }

    public void onClose() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (connection == null) {
                return;
            }

            try {
                byte[] data = Message.serialize(Client.getId());
                Message message = Message.create(Message.Type.LEAVE.getType(), data);

                connection.send(message);
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

}
