package com.github.lbovolini.crowd.core.message.messages;

import java.io.*;

public class InvokeMethod implements Serializable {

    private final int requestId;
    private final String method;
    private final Class<?>[] parameterTypes;
    private final Object[] args;

    public InvokeMethod(int requestId, String method, Class<?>[] parameterTypes, Object[] args) {
        this.requestId = requestId;
        this.method = method;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getMethod() {
        return method;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArgs() {
        return args;
    }
}
