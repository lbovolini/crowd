package com.github.lbovolini.crowd.group;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Scheduler implements Runnable {

    private final BlockingDeque<Request> requests;
    private final MulticastRequestHandler multicastRequestHandler;

    public Scheduler(MulticastRequestHandler multicastRequestHandler) {
        this.requests = new LinkedBlockingDeque<>();
        this.multicastRequestHandler = multicastRequestHandler;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Request request = requests.take();
                multicastRequestHandler.handle(request);
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
