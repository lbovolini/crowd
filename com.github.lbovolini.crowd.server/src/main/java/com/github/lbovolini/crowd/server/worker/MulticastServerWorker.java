package com.github.lbovolini.crowd.server.worker;

import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.monitor.Watcher;
import com.github.lbovolini.crowd.discovery.monitor.WatcherHandler;
import com.github.lbovolini.crowd.discovery.worker.MulticastWorker;
import com.github.lbovolini.crowd.discovery.worker.MulticastWorkerContext;

public class MulticastServerWorker extends MulticastWorker {

    public static final String CODEBASE_ROOT = System.getProperty("codebase.root", "");

    public MulticastServerWorker(MulticastWorkerContext context) {
        super(context);
    }

    @Override
    public final void onInit() {
        super.onInit();

        new Watcher(CODEBASE_ROOT, new WatcherHandler() {
            @Override
            public void onCreate() {
                context.getConnection().multicastSend(MulticastMessageType.UPDATE);
            }

            @Override
            public void onModify() {
                context.getConnection().multicastSend(MulticastMessageType.RELOAD);
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
