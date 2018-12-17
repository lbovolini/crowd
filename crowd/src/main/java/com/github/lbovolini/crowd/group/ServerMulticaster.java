package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.classloader.Watcher;
import com.github.lbovolini.crowd.classloader.WatcherHandler;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;


import static com.github.lbovolini.crowd.configuration.Config.*;

public class ServerMulticaster extends Multicaster {

    public ServerMulticaster() {
        super(MULTICAST_PORT);
    }

    @Override
    public void handle(ServerResponse serverResponse) {

    }

    @Override
    protected void handle(final DatagramChannel channel, String response, InetSocketAddress address) {

        if (response.length() > 1) {
            return;
        }
        if (DISCOVER.equals(response)) {
            join(address);
            responseFromTo(ResponseFactory.get(CONNECT), channel, address);
        }
        else if (HEARTBEAT.equals(response)) {
            if (isMember(address)) {
                responseFromTo(ResponseFactory.get(HEARTBEAT), channel, address);
            } else {
                join(address);
                responseFromTo(ResponseFactory.get(CONNECT), channel, address);
            }
        }
    }

    @Override
    protected void startScheduler(DatagramChannel channel) {
        new Watcher(CODEBASE_ROOT, new WatcherHandler() {
            @Override
            public void onCreate() {
                responseFromTo(ResponseFactory.get(UPDATE), channel, allClients);
                wakeUp();
            }

            @Override
            public void onModify() {
                responseFromTo(ResponseFactory.get(RELOAD), channel, allClients);
                wakeUp();
            }

            @Override
            public void onDelete() {

            }
        }).start();
    }
}
