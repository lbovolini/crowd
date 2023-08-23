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

    private final ExecutorService executor;

    private final ClassLoaderContext classloaderContext;

    private final RequestExecutor requestExecutor;

    public ClientRequestHandler(ClassLoaderContext classloaderContext) {
        this.classloaderContext = classloaderContext;
        this.executor = Executors.newSingleThreadExecutor(this.classloaderContext.getThreadFactory());
        this.requestExecutor = new RequestExecutor();
    }

    @Override
    public void handle(Request request) {

        executor.submit(() -> {
            Message message = request.getMessage();
            MessageType type = MessageType.get(message.getType());

            switch (type) {
                case CREATE -> requestExecutor.create(request);
                case INVOKE -> requestExecutor.execute(request);
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
}
