package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.monitor.Watcher;
import com.github.lbovolini.crowd.monitor.WatcherHandler;

import java.net.InetSocketAddress;

import static com.github.lbovolini.crowd.configuration.Config.*;

public class ServerMulticast extends Multicast {

    public ServerMulticast() {
        super(MULTICAST_PORT);
    }

    @Override
    protected void scheduler() {
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

    /**
     * handle mensagem do cliente
     * @param response
     * @param address
     */
    @Override
    protected void handle(String response, InetSocketAddress address) {

        if (response.length() > 1) {
            return;
        }
        if (DISCOVER.equals(response)) {
            join(address);
            response(CONNECT, address);
        }
        else if (HEARTBEAT.equals(response)) {
            if (isMember(address)) {
                response(HEARTBEAT, address);
            } else {
                join(address);
                response(CONNECT, address);
            }
        }
    }

    public static void main(String[] args) {
        ServerMulticast serverMulticaster = new ServerMulticast();
        serverMulticaster.start();
    }

}
