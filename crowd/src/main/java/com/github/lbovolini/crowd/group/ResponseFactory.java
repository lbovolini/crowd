package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.utils.CodebaseUtils;

import java.nio.charset.StandardCharsets;

import static com.github.lbovolini.crowd.configuration.Config.*;

public class ResponseFactory {

    public static byte[] get(String response) {

        if (response.equals(HEARTBEAT)) {
            return HEARTBEAT.getBytes(StandardCharsets.UTF_8);
        }
        if (response.equals(DISCOVER)) {
            return DISCOVER.getBytes(StandardCharsets.UTF_8);
        }
        if (response.equals(UPDATE)) {
            return of(UPDATE);
        }
        if (response.equals(CONNECT)) {
            return of(CONNECT);
        }
        if (response.equals(RELOAD)) {
            return of(RELOAD);
        }

        return new byte[] {};
    }

    private static byte[] of(String type) {

        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();

        return (codebase + SEPARATOR
                + HOST_NAME + SEPARATOR
                + PORT + SEPARATOR
                + libURL + SEPARATOR
                + type).getBytes(StandardCharsets.UTF_8);

    }
}
