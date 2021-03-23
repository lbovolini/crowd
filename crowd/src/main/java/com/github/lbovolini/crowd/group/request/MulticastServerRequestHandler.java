package com.github.lbovolini.crowd.group.request;

import com.github.lbovolini.crowd.group.connection.MulticastConnection;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.lbovolini.crowd.group.message.MulticastMessageType.*;

public class MulticastServerRequestHandler implements MulticastRequestHandler {

    private static final Set<String> hosts = ConcurrentHashMap.newKeySet();

    @Override
    public void handle(MulticastRequest request) {

        if (request.getMessage().getDataLength() > 1) {
            return;
        }

        byte type = request.getMessage().getType();
        InetSocketAddress address = request.getMessage().getAddress();

        MulticastConnection connection = request.getConnection();

        if (DISCOVER.getType() == type) {
            join(address);
            connection.send(CONNECT, address);
        }
        else if (HEARTBEAT.getType() == type) {
            if (isMember(address)) {
                connection.send(HEARTBEAT, address);
            } else {
                join(address);
                connection.send(CONNECT, address);
            }
        }
    }

    /**
     * Adiciona endereço do Agent ao conjunto de endereços
     * @param address
     */
    private void join(InetSocketAddress address) {
        hosts.add(address.toString());
    }

    /**
     * Verifica se o endereço está presente no conjunto de endereços
     * @param address
     * @return
     */
    private boolean isMember(InetSocketAddress address) {
        return hosts.contains(address.toString());
    }
}
