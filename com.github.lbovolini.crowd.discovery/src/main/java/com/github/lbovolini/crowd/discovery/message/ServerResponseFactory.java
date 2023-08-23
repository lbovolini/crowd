package com.github.lbovolini.crowd.discovery.message;

import com.github.lbovolini.crowd.discovery.exception.InvalidMulticastMessageException;
import com.github.lbovolini.crowd.discovery.util.CodebaseUtils;

import java.nio.charset.StandardCharsets;

public class ServerResponseFactory implements ResponseFactory {

    private final String hostname;
    private final int port;

    public ServerResponseFactory(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public byte[] get(MulticastMessageType type) {

        if (type.equals(MulticastMessageType.HEARTBEAT)) {
            return new byte[] { MulticastMessageType.HEARTBEAT.getType() };
        }
        if (type.equals(MulticastMessageType.UPDATE)) {
            return of(MulticastMessageType.UPDATE);
        }
        if (type.equals(MulticastMessageType.CONNECT)) {
            return of(MulticastMessageType.CONNECT);
        }
        if (type.equals(MulticastMessageType.RELOAD)) {
            return of(MulticastMessageType.RELOAD);
        }

        throw new InvalidMulticastMessageException("Invalid multicast message type exception");
    }

    // !TODO pq type por ultimo?
    private byte[] of(MulticastMessageType type) {

        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        return (codebase + SEPARATOR
                + hostname + SEPARATOR
                + port + SEPARATOR
                + libURL + SEPARATOR
                + type.getType()).getBytes(StandardCharsets.UTF_8);

    }
}
