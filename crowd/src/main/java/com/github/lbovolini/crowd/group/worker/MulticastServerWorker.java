package com.github.lbovolini.crowd.group.worker;

import com.github.lbovolini.crowd.group.monitor.Watcher;
import com.github.lbovolini.crowd.group.monitor.WatcherHandler;

import static com.github.lbovolini.crowd.configuration.Config.*;
import static com.github.lbovolini.crowd.group.message.MulticastMessageType.RELOAD;
import static com.github.lbovolini.crowd.group.message.MulticastMessageType.UPDATE;

public class MulticastServerWorker extends MulticastWorker {

    public MulticastServerWorker(MulticastWorkerContext context) {
        super(context);
    }

    @Override
    public final void onInit() {
        super.onInit();

        new Watcher(CODEBASE_ROOT, new WatcherHandler() {
            @Override
            public void onCreate() {
                context.getConnection().multicastSend(UPDATE);
            }

            @Override
            public void onModify() {
                context.getConnection().multicastSend(RELOAD);
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
