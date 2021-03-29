package com.github.lbovolini.crowd.discovery.request;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class MulticastScheduler implements Runnable {

    private final BlockingDeque<MulticastRequest> requests;
    private final MulticastRequestHandler multicastRequestHandler;

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public MulticastScheduler(MulticastRequestHandler multicastRequestHandler) {
        this.requests = new LinkedBlockingDeque<>();
        this.multicastRequestHandler = multicastRequestHandler;
        this.pool.submit(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                MulticastRequest request = requests.take();
                multicastRequestHandler.handle(request);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public boolean enqueue(MulticastRequest request) {
        return requests.offer(request);
    }
}
