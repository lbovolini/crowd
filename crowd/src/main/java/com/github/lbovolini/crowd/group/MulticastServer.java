package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.monitor.Watcher;
import com.github.lbovolini.crowd.monitor.WatcherHandler;

import static com.github.lbovolini.crowd.configuration.Config.*;

public class MulticastServer extends Multicast {

    public MulticastServer(MulticastRequestHandler multicastRequestHandler) {
        super(MULTICAST_PORT, multicastRequestHandler, true);
    }

    @Override
    public final void onInit() {
        super.onInit();

        new Watcher(CODEBASE_ROOT, new WatcherHandler() {
            @Override
            public void onCreate() {
                sendAll(UPDATE);
            }

            @Override
            public void onModify() {
                sendAll(RELOAD);
            }

            @Override
            public void onDelete() {

            }
        }).start();
    }

    @Override
    public final void onReceive() {
        super.onReceive();
    }
}
