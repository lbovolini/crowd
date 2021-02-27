package com.github.lbovolini.crowd.scheduler;

import com.github.lbovolini.crowd.Servant;
import com.github.lbovolini.crowd.classloader.Context;
import com.github.lbovolini.crowd.classloader.RemoteClassLoader;
import com.github.lbovolini.crowd.classloader.RemoteClassLoaderService;
import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.messages.CreateObject;
import com.github.lbovolini.crowd.message.messages.InvokeMethod;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.lbovolini.crowd.configuration.Config.POOL_SIZE;

/**
 * Responsável por manipular as requisições recebidas pelo cliente.
 */
public class ClientRequestHandler implements RequestHandler {

    private Object object;
    private ExecutorService pool;

    private final ExecutorService executor;

    private final Object lock = new Object();
    private MessageFrom latestCreatedObject;
    private final Context context;

    public ClientRequestHandler(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor(this.context.getThreadFactory());
    }


    private static Object getObject(Message message) throws IOException, ClassNotFoundException {
        return Message.deserialize(message.getData());
    }

    private void create(MessageFrom messageFrom) throws Exception {
        setLatestCreatedObject(messageFrom);
        stop();
        CreateObject createObject = (CreateObject)getObject(messageFrom.getMessage());
        object = newObject(createObject.getName(), createObject.getParameterTypes(), createObject.getArgs());
    }


    private void invoke(MessageFrom messageFrom) throws IOException, ClassNotFoundException {
        InvokeMethod invokeMethod = (InvokeMethod)getObject(messageFrom.getMessage());
        pool.execute(() -> Servant.execute(object, invokeMethod, messageFrom.getConnection()));
    }


    @Override
    public void handle(MessageFrom messageFrom) {

        executor.submit(() -> {
            Message message = messageFrom.getMessage();
            Message.Type type = Message.Type.get(message.getType());

            switch (type) {
                case CREATE: {
                    try {
                        create(messageFrom);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case INVOKE: {
                    try {
                        invoke(messageFrom);
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

    private void setLatestCreatedObject(MessageFrom messageFrom) {
        this.latestCreatedObject = messageFrom;
    }

    public MessageFrom getLatestCreatedObject() {
        synchronized (lock) {
            return latestCreatedObject;
        }
    }

    public Context getContext() {
        return context;
    }
}
