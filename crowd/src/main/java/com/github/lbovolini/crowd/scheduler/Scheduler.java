package com.github.lbovolini.crowd.scheduler;

import com.github.lbovolini.crowd.classloader.ThreadRemoteClassLoaderService;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Scheduler implements Runnable {

    private final BlockingDeque<MessageFrom> messages;
    private final RequestHandler handler;
    private final Thread thread;
    private final ThreadRemoteClassLoaderService loaderService;

    public Scheduler(RequestHandler requestHandler) {
        this.handler = requestHandler;
        this.messages = new LinkedBlockingDeque<>();
        this.thread = new Thread(this);
        this.loaderService = new ThreadRemoteClassLoaderService(thread);
    }

    @Override
    public void run() {
        while (true) {
            try {
                MessageFrom messageFrom = messages.take();
                handler.handle(messageFrom);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public boolean enqueue(MessageFrom messageFrom) {
        return messages.offer(messageFrom);
    }

    public void start() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public void create(String codebase) {
        loaderService.newClassLoader(codebase);
    }

    public void update(String codebase) {
        loaderService.updateCodebaseURLs(codebase);
    }

    public void reload(String codebase) {
        loaderService.reload(codebase);
        MessageFrom messageFrom = ((ClientRequestHandler)handler).getLatestCreatedObject();
        enqueue(messageFrom);
    }

}
