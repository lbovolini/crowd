package com.github.lbovolini.crowd.core.message.messages;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InvokeMethod that = (InvokeMethod) o;

        return requestId == that.requestId
                && Objects.equals(method, that.method)
                && Arrays.equals(parameterTypes, that.parameterTypes)
                && Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(requestId, method);

        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(args);

        return result;
    }
}
