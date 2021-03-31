package com.github.lbovolini.crowd.core.request;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class Scheduler implements Runnable, RequestQueue {

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    private final BlockingDeque<Request> messages;
    private final RequestHandler requestHandler;

    public Scheduler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        this.messages = new LinkedBlockingDeque<>();
        this.pool.submit(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Request request = messages.take();
                requestHandler.handle(request);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public boolean enqueue(Request request) {
        return messages.offer(request);
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public void stop() {
    }
}
