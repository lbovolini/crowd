package com.github.lbovolini.crowd.group.worker;

public class MulticastClientWorker extends MulticastWorker {

    private final TimeScheduler timeScheduler;

    public MulticastClientWorker(MulticastWorkerContext context) {
        super(context);
        timeScheduler = new TimeScheduler(context.getConnection());
    }

    @Override
    public final void onInit() {
        timeScheduler.start();
    }

    @Override
    public final void onReceive() {
        timeScheduler.updateLastResponseTime();
    }

}
