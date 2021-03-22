package com.github.lbovolini.crowd.group.message;

import com.github.lbovolini.crowd.utils.CodebaseUtils;

import java.nio.charset.StandardCharsets;

import static com.github.lbovolini.crowd.configuration.Config.*;
import static com.github.lbovolini.crowd.group.message.MulticastMessageType.*;


public class ResponseFactory {

    public static byte[] get(MulticastMessageType type) {

        if (type.equals(HEARTBEAT)) {
            return new byte[] { HEARTBEAT.getType() };
        }
        if (type.equals(DISCOVER)) {
            return new byte[] { DISCOVER.getType() };
        }
        if (type.equals(UPDATE)) {
            return of(UPDATE);
        }
        if (type.equals(CONNECT)) {
            return of(CONNECT);
        }
        if (type.equals(RELOAD)) {
            return of(RELOAD);
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
