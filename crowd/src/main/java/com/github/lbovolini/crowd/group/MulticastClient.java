package com.github.lbovolini.crowd.group;

import static com.github.lbovolini.crowd.configuration.Config.*;

public class MulticastClient extends Multicast {

    private final TimeScheduler timeScheduler = new TimeScheduler(this);

    public MulticastClient(MulticastRequestHandler multicastRequestHandler) {
        super(MULTICAST_CLIENT_PORT, multicastRequestHandler, false);
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
