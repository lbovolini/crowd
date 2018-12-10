package com.github.lbovolini.crowd.common.group;

import com.github.lbovolini.crowd.common.classloader.CodebaseUtils;

import static com.github.lbovolini.crowd.common.configuration.Config.*;

public class ResponseFactory {
    public static String get(Object response) {
        if (response.equals(HEARTBEAT)) {
            return HEARTBEAT;
        }
        if (response.equals(DISCOVER)) {
            return DISCOVER;
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
        return new String();
    }

    private static String of(String TYPE) {
        String codebase = CodebaseUtils.getCodebaseURLs();
        return codebase + SEPARATOR + HOST_NAME + SEPARATOR + PORT + SEPARATOR + LIB_URL + SEPARATOR + TYPE;
    }
}
