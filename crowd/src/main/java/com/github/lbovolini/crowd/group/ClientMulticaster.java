package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.message.Message;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.github.lbovolini.crowd.configuration.Config.*;

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
        // !todo
        if (response.length() > 1) {
            ServerResponse serverResponse = ServerResponse.fromObject(response);
            setServerAddress(serverResponse);
            //Message message = Message.create(Byte.valueOf(serverResponse.getType()), response.getBytes(StandardCharsets.UTF_8));
            handle(serverResponse);
        }
    }

    private void setServerAddress(ServerResponse serverResponse) {
        this.serverAddress = serverResponse.getServerAddress();
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
