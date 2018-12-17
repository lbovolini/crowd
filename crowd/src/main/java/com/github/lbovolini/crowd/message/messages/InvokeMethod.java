package com.github.lbovolini.crowd.message.messages;

import java.io.*;

public class InvokeMethod implements Serializable {

    private final int requestId;
    private final String method;
    private final Object[] args;

    public InvokeMethod(int requestId, String method, Object[] args) {
        this.requestId = requestId;
        this.method = method;
        this.args = args;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
