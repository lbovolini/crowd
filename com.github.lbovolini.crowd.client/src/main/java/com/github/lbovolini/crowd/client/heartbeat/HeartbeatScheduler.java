package com.github.lbovolini.crowd.client.heartbeat;

import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatScheduler {

    public static final int HEARTBEAT_INTERVAL = 5;
    public static final int MAX_DOWNTIME = 15;

    private long lastResponseTime = 0;
    private final Object lock = new Object();

    private final MulticastConnection connection;
    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    public HeartbeatScheduler(MulticastConnection connection) {
        this.connection = connection;
    }

    public void start() {
        pool.scheduleWithFixedDelay(() -> {
            if (isDownTimeExceeded()) {
                connection.multicastSend(MulticastMessageType.DISCOVER);
            } else {
                connection.send(MulticastMessageType.HEARTBEAT);
            }
        }, 0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    public void updateLastResponseTime() {
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
