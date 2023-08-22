package com.github.lbovolini.crowd.discovery.request;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Apenas seleciona a requisicao da fila
 */
public class MulticastScheduler implements Runnable, MulticastRequestQueue {

    private final BlockingDeque<MulticastRequest> requests;
    private final MulticastDispatcher multicastDispatcher;

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public MulticastScheduler(MulticastDispatcher multicastDispatcher) {
        this.requests = new LinkedBlockingDeque<>();
        this.multicastDispatcher = multicastDispatcher;
        this.pool.submit(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                MulticastRequest request = requests.take();
                multicastDispatcher.dispatch(request);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public boolean enqueue(MulticastRequest request) {
        return requests.offer(request);
    }
}
