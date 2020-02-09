package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.utils.CodebaseUtils;

import static com.github.lbovolini.crowd.configuration.Config.*;

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
        return "";
    }

    private static String of(String TYPE) {
        String codebase = CodebaseUtils.getCodebaseURLs();
        String libURL = CodebaseUtils.getLibURL();
        return codebase + SEPARATOR + HOST_NAME + SEPARATOR + PORT + SEPARATOR + libURL + SEPARATOR + TYPE;

    }
}
