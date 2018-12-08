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
import java.util.concurrent.*;

public class Scheduler implements Proxy, Service {

    private Object object;
    private Connection connection;

    private final Thread thread;

    private final BlockingDeque<Message> requests;
    private ExecutorService pool;

    private String codebase;
    private Message latestCreatedObject;
    private final Object lock = new Object();

    private RemoteClassLoader classLoader;

    public Scheduler(String codebase) {
        this.requests = new LinkedBlockingDeque<>();
        this.thread = new Thread(this::dispatch);
        this.codebase = codebase;
        reload(codebase);
        onClose();
        classLoader = new RemoteClassLoader(codebase, Scheduler.class.getClassLoader());
    }

    public void reload(String codebase) {
        synchronized (lock) {
            this.codebase = codebase;
        }
        recreateObject();
    }

    public void addURL(String codebase) {
        synchronized (lock) {
            this.codebase = codebase;
            classLoader.addURLs(codebase);
        }
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
                Thread.currentThread().setContextClassLoader(classLoader);

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
                    latestCreatedObject = message;
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
        String codebase = getCodebase();
        classLoader = new RemoteClassLoader(codebase, Scheduler.class.getClassLoader());
        Class classDefinition = classLoader.loadClass(className);
        Constructor constructor = classDefinition.getConstructor(getTypes(args));

        return constructor.newInstance(args);
    }

    private String getCodebase() {
        synchronized (lock) {
            return this.codebase;
        }
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

    private void recreateObject() {
        if (latestCreatedObject != null) {
            requests.offer(latestCreatedObject);
        }
    }

    public void stop() {
        if (pool != null) {
            try {
                pool.shutdown();
                pool.awaitTermination(1, TimeUnit.SECONDS);
                pool.shutdownNow();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        pool = Executors.newFixedThreadPool(Client.getCores());
    }

    public void kill() {

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
