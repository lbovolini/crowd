package com.github.lbovolini.crowd.client.worker;

import com.github.lbovolini.crowd.client.heartbeat.HeartbeatScheduler;
import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.worker.MulticastWorker;

public class MulticastClientWorker extends MulticastWorker {

    private final HeartbeatScheduler heartbeatScheduler;

    public MulticastClientWorker(MulticastConnection connection) {
        super(connection);
        heartbeatScheduler = new HeartbeatScheduler(connection);
    }

    @Override
    public final void onInit() {
        heartbeatScheduler.start();
    }

    @Override
    public final void onReceive() {
        heartbeatScheduler.updateLastResponseTime();
    }

}
