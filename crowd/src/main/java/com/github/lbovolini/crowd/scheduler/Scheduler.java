package com.github.lbovolini.crowd.scheduler;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Scheduler implements Runnable {

    private final Thread thread;

    private final BlockingDeque<MessageFrom> messages;
    private final Dispatcher dispatcher;


        //this(dispatcher, System.getProperty("java.class.path"), System.getProperty("java.library.path"));

    public Scheduler(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.messages = new LinkedBlockingDeque<>();
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                MessageFrom messageFrom = messages.take();
                dispatcher.dispatch(messageFrom);
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

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

}
