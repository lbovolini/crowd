package com.github.lbovolini.crowd.message.messages;

import java.io.Serializable;

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

}
