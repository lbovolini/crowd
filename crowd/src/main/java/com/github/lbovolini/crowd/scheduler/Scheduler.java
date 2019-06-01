package com.github.lbovolini.crowd.scheduler;

import com.github.lbovolini.crowd.classloader.ThreadRemoteClassLoaderService;

import java.net.URL;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Scheduler implements Runnable {

    private final Thread thread;
    private final ThreadRemoteClassLoaderService loaderService;

    private final BlockingDeque<MessageFrom> messages;
    private final RequestHandler handler;

    public Scheduler(RequestHandler requestHandler) {
        this(requestHandler, "", "");
    }

    public Scheduler(RequestHandler requestHandler, String classPath, String libPath) {
        this.handler = requestHandler;
        this.messages = new LinkedBlockingDeque<>();
        this.thread = new Thread(this);
        this.loaderService = new ThreadRemoteClassLoaderService(thread, classPath, libPath);
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

    public void create(URL[] classURLs, URL libURL) {
        loaderService.create(classURLs, libURL);
    }

    public void update(URL[] classURLs, URL libURL) {
        //loaderService.updateCodebaseURLs(codebase);
    }

    public void reload(URL[] classURLs, URL libURL) {
        //loaderService.reload(classURLs, libURL);
        MessageFrom messageFrom = ((ClientRequestHandler)handler).getLatestCreatedObject();
        enqueue(messageFrom);
    }

}
