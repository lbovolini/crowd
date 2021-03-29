package com.github.lbovolini.crowd.discovery.request;

import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

        if (MulticastMessageType.DISCOVER.getType() == type) {
            join(address);
            connection.send(MulticastMessageType.CONNECT, address);
        }
        else if (MulticastMessageType.HEARTBEAT.getType() == type) {
            if (isMember(address)) {
                connection.send(MulticastMessageType.HEARTBEAT, address);
            } else {
                join(address);
                connection.send(MulticastMessageType.CONNECT, address);
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
