package com.github.lbovolini.crowd.core.message.messages;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Response response = (Response) o;

        return requestId == response.requestId
                && Objects.equals(result, response.result)
                && Objects.equals(exception, response.exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, result, exception);
    }
}
