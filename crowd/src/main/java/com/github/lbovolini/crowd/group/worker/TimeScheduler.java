package com.github.lbovolini.crowd.group.worker;

import com.github.lbovolini.crowd.group.connection.MulticastConnection;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.lbovolini.crowd.configuration.Config.*;
import static com.github.lbovolini.crowd.configuration.Config.HEARTBEAT_INTERVAL;
import static com.github.lbovolini.crowd.group.message.MulticastMessageType.DISCOVER;
import static com.github.lbovolini.crowd.group.message.MulticastMessageType.HEARTBEAT;

public class TimeScheduler {

    private long lastResponseTime = 0;
    private final Object lock = new Object();

    private final MulticastConnection connection;
    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    public TimeScheduler(MulticastConnection connection) {
        this.connection = connection;
    }

    protected void start() {
        pool.scheduleWithFixedDelay(() -> {
            if (isDownTimeExceeded()) {
                connection.multicastSend(DISCOVER);
            } else {
                connection.send(HEARTBEAT);
            }
        }, 0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    protected void updateLastResponseTime() {
        synchronized (lock) {
            lastResponseTime = System.nanoTime();
        }
    }

    private boolean isDownTimeExceeded() {

        long downTime;

        synchronized (lock) {
            downTime = System.nanoTime() - lastResponseTime;
        }

        return TimeUnit.NANOSECONDS.toSeconds(downTime) > MAX_DOWNTIME;
    }

}
