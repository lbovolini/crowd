package com.github.lbovolini.crowd.common.message.response;

import java.io.Serializable;
import java.util.UUID;

public class Response implements Serializable {
    private final String from;
    private final UUID uuid;
    private final Object result;
    private final Exception exception;

    public Response(String from, UUID uuid, Object result, Exception exception) {
        this.from = from;
        this.uuid = uuid;
        this.result = result;
        this.exception = exception;
    }

    public String getFrom() {
        return from;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Object getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }


}
