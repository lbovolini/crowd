package com.github.lbovolini.crowd.core.message.messages;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class CreateObject implements Serializable {

    private final String name;
    private final Class<?>[] parameterTypes;
    private final Object[] args;

    public CreateObject(String name, Class<?>[] parameterTypes, Object[] args) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.args = args;
    }

    public String getName() {
        return name;
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

        CreateObject that = (CreateObject) o;

        return Objects.equals(name, that.name)
                && Arrays.equals(parameterTypes, that.parameterTypes)
                && Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);

        result = 31 * result + Arrays.hashCode(parameterTypes);
        result = 31 * result + Arrays.hashCode(args);

        return result;
    }
}
