package com.github.lbovolini.crowd.group;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.lbovolini.crowd.configuration.Config.*;

public class MulticastServerRequestHandler implements MulticastRequestHandler {

    private static final Set<String> hosts = ConcurrentHashMap.newKeySet();

    @Override
    public void handle(Request request) {

        if (request.getMessage().getDataLength() > 1) {
            return;
        }

        String response = request.getMessage().getDataAsString();
        InetSocketAddress address = request.getMessage().getAddress();

        Connection connection = request.getConnection();

        if (DISCOVER.equals(response)) {
            join(address);
            connection.sendToHost(CONNECT, address);
        }
        else if (HEARTBEAT.equals(response)) {
            if (isMember(address)) {
                connection.sendToHost(HEARTBEAT, address);
            } else {
                join(address);
                connection.sendToHost(CONNECT, address);
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
