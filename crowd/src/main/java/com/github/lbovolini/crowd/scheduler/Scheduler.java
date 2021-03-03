package com.github.lbovolini.crowd.scheduler;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Scheduler implements Runnable {

    private final Thread thread;

    private final BlockingDeque<Request> messages;
    private final RequestHandler requestHandler;

    public Scheduler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        this.messages = new LinkedBlockingDeque<>();
        this.thread = new Thread(this);
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

    public void start() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }
}
