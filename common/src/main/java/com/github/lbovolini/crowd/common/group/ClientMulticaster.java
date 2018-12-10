package com.github.lbovolini.crowd.common.group;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.TimeUnit;

import static com.github.lbovolini.crowd.common.configuration.Config.*;

public class ClientMulticaster extends Multicaster {

    private String serverAddress;

    public ClientMulticaster() {
        super(MULTICAST_CLIENT_PORT);
    }

    @Override
    public void handle(ServerResponse serverResponse) {

    }

    @Override
    protected void handle(final DatagramChannel channel, String response, InetSocketAddress address) {

        // !todo
        updateLastResponseTime();

        if (response.length() > 1) {
            ServerResponse serverResponse = ServerResponse.fromObject(response);
            setServerAddress(serverResponse);
            handle(serverResponse);
        }
    }

    private void setServerAddress(ServerResponse serverResponse) {
        if (this.serverAddress == null) {
            this.serverAddress = serverResponse.getServerAddress();
        }
    }

    @Override
    protected void startScheduler(DatagramChannel channel) {
        pool.scheduleWithFixedDelay(() -> {
            if (isDownTimeExceeded()) {
                responseFromTo(ResponseFactory.get(DISCOVER), channel, new InetSocketAddress(MULTICAST_IP, MULTICAST_PORT));
            } else {
                responseFromTo(ResponseFactory.get(HEARTBEAT), channel, new InetSocketAddress(serverAddress, MULTICAST_PORT));
            }
            wakeUp();
        }, 0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }
}
