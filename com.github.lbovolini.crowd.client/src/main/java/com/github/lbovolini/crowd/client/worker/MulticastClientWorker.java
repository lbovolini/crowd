package com.github.lbovolini.crowd.client.worker;

import com.github.lbovolini.crowd.client.heartbeat.HeartbeatScheduler;
import com.github.lbovolini.crowd.discovery.worker.MulticastWorker;
import com.github.lbovolini.crowd.discovery.worker.MulticastWorkerContext;

public class MulticastClientWorker extends MulticastWorker {

    private final HeartbeatScheduler heartbeatScheduler;

    public MulticastClientWorker(MulticastWorkerContext context) {
        super(context);
        heartbeatScheduler = new HeartbeatScheduler(context.getConnection());
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
