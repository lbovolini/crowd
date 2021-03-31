package com.github.lbovolini.crowd.discovery.message;

import com.github.lbovolini.crowd.discovery.util.CodebaseUtils;

import java.nio.charset.StandardCharsets;

public class ResponseFactory {

    // !TODO
    public static final String HOST_NAME = System.getProperty("hostname", "");
    public static final int PORT = Integer.parseInt(System.getProperty("port", String.valueOf(8081)));
    public static final String SEPARATOR = ";";

    private ResponseFactory() {}

    public static byte[] get(MulticastMessageType type) {

        if (type.equals(MulticastMessageType.HEARTBEAT)) {
            return new byte[] { MulticastMessageType.HEARTBEAT.getType() };
        }
        if (type.equals(MulticastMessageType.DISCOVER)) {
            return new byte[] { MulticastMessageType.DISCOVER.getType() };
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

        return new byte[] {};
    }

    // !TODO pq type por ultimo?
    private static byte[] of(MulticastMessageType type) {

        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        return (codebase + SEPARATOR
                + HOST_NAME + SEPARATOR
                + PORT + SEPARATOR
                + libURL + SEPARATOR
                + type.getType()).getBytes(StandardCharsets.UTF_8);

    }
}
