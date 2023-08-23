package com.github.lbovolini.crowd.client.request;

import com.github.lbovolini.crowd.client.runner.Servant;
import com.github.lbovolini.crowd.core.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RequestExecutor {

    private static final Logger log = LoggerFactory.getLogger(RequestExecutor.class);

    public static final int POOL_SIZE = Integer.parseInt(System.getProperty("pool.size",  String.valueOf(Runtime.getRuntime().availableProcessors())));

    private Object object;
    private ExecutorService pool;

//    private final Object lock = new Object();
//    private Request latestCreatedObject;

    public void create(Request request) {
        destroy();
        object = HandleCreateRequest.handle(request);
//                    if (object == null) {
//                        return;
//                    }
//                    setLatestCreatedObject(request);
    }

    public void destroy() {
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

    public void execute(Request request) {
        var invokeMethod = HandleInvokeRequest.handle(request);
        if (invokeMethod == null) {
            return;
        }
        pool.execute(() -> Servant.execute(object, invokeMethod, request.getConnection()));
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
