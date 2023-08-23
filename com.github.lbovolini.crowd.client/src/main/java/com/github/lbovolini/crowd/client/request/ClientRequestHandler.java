package com.github.lbovolini.crowd.client.request;

import com.github.lbovolini.crowd.classloader.ClassLoaderContext;
import com.github.lbovolini.crowd.client.runner.Servant;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageType;
import com.github.lbovolini.crowd.core.request.Request;
import com.github.lbovolini.crowd.core.request.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Responsável por manipular as requisições recebidas pelo cliente.
 */
public class ClientRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientRequestHandler.class);

    public static final int POOL_SIZE = Integer.parseInt(System.getProperty("pool.size",  String.valueOf(Runtime.getRuntime().availableProcessors())));

    private Object object;
    private ExecutorService pool;

    private final ExecutorService executor;

//    private final Object lock = new Object();
//    private Request latestCreatedObject;
    private final ClassLoaderContext classloaderContext;

    public ClientRequestHandler(ClassLoaderContext classloaderContext) {
        this.classloaderContext = classloaderContext;
        this.executor = Executors.newSingleThreadExecutor(this.classloaderContext.getThreadFactory());
    }

    @Override
    public void handle(Request request) {

        executor.submit(() -> {
            Message message = request.getMessage();
            MessageType type = MessageType.get(message.getType());

            switch (type) {
                case CREATE -> {
                    stop();
                    object = HandleCreateRequest.handle(request);
//                    if (object == null) {
//                        return;
//                    }
//                    setLatestCreatedObject(request);
                }
                case INVOKE -> {
                    var invokeMethod = HandleInvokeRequest.handle(request);
                    if (invokeMethod == null) {
                        return;
                    }
                    pool.execute(() -> Servant.execute(object, invokeMethod, request.getConnection()));
                }

                //case SERVICE: {
                //    service(messageFrom);
                //    break;
                //}
                default -> {
                    log.info("UNKNOWN MESSAGE TYPE");
                }
            }
        });
    }

    public void stop() {
        if (pool != null) {
            try {
                pool.shutdown();
                pool.awaitTermination(1, TimeUnit.SECONDS);
                //pool.shutdownNow();
            } catch (InterruptedException e) {
                log.error("Error while stopping thread pool", e);
            }
        }
        // !todo
        pool = Executors.newFixedThreadPool(POOL_SIZE);
    }

//    private void setLatestCreatedObject(Request request) {
//        this.latestCreatedObject = request;
//    }
//
//    public Request getLatestCreatedObject() {
//        synchronized (lock) {
//            return latestCreatedObject;
//        }
//    }
}
