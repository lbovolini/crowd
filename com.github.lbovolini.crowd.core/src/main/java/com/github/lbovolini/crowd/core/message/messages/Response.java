package com.github.lbovolini.crowd.core.message.messages;

import java.io.Serializable;

public class Response implements Serializable {
    private final int requestId;
    private final Object result;
    private final String exception;

    public Response(int requestId, Object result, String exception) {
        this.requestId = requestId;
        this.result = result;
        this.exception = exception;
    }

    public int getRequestId() {
        return requestId;
    }

    public Object getResult() {
        return result;
    }

    public Exception getException() {
        return exception != null ? new Exception(exception) : null;
    }
}
