package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.worker.WorkerContext;

import java.util.concurrent.*;

public class IOChannelScheduler implements Runnable {

    private final ExecutorService pool;

    private final BlockingDeque<WorkerContext> workerContextBlockingDeque;
    private final IOChannelHandler ioChannelHandler;

    public IOChannelScheduler(IOChannelHandler ioChannelHandler) {
        this.ioChannelHandler = ioChannelHandler;
        this.pool = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "IOChannelScheduler"));
        this.workerContextBlockingDeque = new LinkedBlockingDeque<>();
        this.pool.submit(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                WorkerContext workerContext = workerContextBlockingDeque.take();
                ioChannelHandler.handle(workerContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean enqueue(WorkerContext workerContext) {
        return workerContextBlockingDeque.offer(workerContext);
    }
}
