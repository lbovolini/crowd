package com.github.lbovolini.crowd.client.request;

import com.github.lbovolini.crowd.client.runner.Servant;
import com.github.lbovolini.crowd.classloader.ClassLoaderContext;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.messages.CreateObject;
import com.github.lbovolini.crowd.core.message.messages.InvokeMethod;
import com.github.lbovolini.crowd.core.request.Request;
import com.github.lbovolini.crowd.core.request.RequestHandler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Responsável por manipular as requisições recebidas pelo cliente.
 */
public class ClientRequestHandler implements RequestHandler {

    public static final int POOL_SIZE = Integer.parseInt(System.getProperty("pool.size",  String.valueOf(Runtime.getRuntime().availableProcessors())));

    private Object object;
    private ExecutorService pool;

    private final ExecutorService executor;

    private final Object lock = new Object();
    private Request latestCreatedObject;
    private final ClassLoaderContext classloaderContext;

    public ClientRequestHandler(ClassLoaderContext classloaderContext) {
        this.classloaderContext = classloaderContext;
        this.executor = Executors.newSingleThreadExecutor(this.classloaderContext.getThreadFactory());
    }


    private static Object getObject(Message message) throws IOException, ClassNotFoundException {
        return Message.deserialize(message.getData());
    }

    private void create(Request request) throws Exception {
        setLatestCreatedObject(request);
        stop();
        CreateObject createObject = (CreateObject)getObject(request.getMessage());
        object = newObject(createObject.getName(), createObject.getParameterTypes(), createObject.getArgs());
    }


    private void invoke(Request request) throws IOException, ClassNotFoundException {
        InvokeMethod invokeMethod = (InvokeMethod)getObject(request.getMessage());
        pool.execute(() -> Servant.execute(object, invokeMethod, request.getConnection()));
    }


    @Override
    public void handle(Request request) {

        executor.submit(() -> {
            Message message = request.getMessage();
            Message.Type type = Message.Type.get(message.getType());

            switch (type) {
                case CREATE: {
                    try {
                        create(request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case INVOKE: {
                    try {
                        invoke(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                //case SERVICE: {
                //    service(messageFrom);
                //    break;
                //}
                default: {
                    System.out.println("UNKNOWN MESSAGE TYPE");
                }
            }
        });
    }

    public Object newObject(String className, Class<?>[] parameterTypes, Object[] args) throws Exception {
        Class classDefinition = Thread.currentThread().getContextClassLoader().loadClass(className);
        Constructor constructor = classDefinition.getConstructor(parameterTypes);
        return constructor.newInstance(args);
    }

    public void stop() {
        if (pool != null) {
            try {
                pool.shutdown();
                pool.awaitTermination(1, TimeUnit.SECONDS);
                //pool.shutdownNow();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // !todo
        pool = Executors.newFixedThreadPool(POOL_SIZE);
    }

    private void setLatestCreatedObject(Request request) {
        this.latestCreatedObject = request;
    }

    public Request getLatestCreatedObject() {
        synchronized (lock) {
            return latestCreatedObject;
        }
    }
}
