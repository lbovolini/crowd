package com.github.lbovolini.crowd.group;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.lbovolini.crowd.configuration.Config.*;
import static com.github.lbovolini.crowd.configuration.Config.HEARTBEAT_INTERVAL;

public class TimeScheduler {

    private long lastResponseTime = 0;
    private final Object lock = new Object();

    private final Multicast multicast;
    private final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

    public TimeScheduler(Multicast multicast) {
        this.multicast = multicast;
    }

    protected void start() {
        pool.scheduleWithFixedDelay(() -> {
            if (isDownTimeExceeded()) {
                multicast.sendAll(DISCOVER);
            } else {
                multicast.send(HEARTBEAT);
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
