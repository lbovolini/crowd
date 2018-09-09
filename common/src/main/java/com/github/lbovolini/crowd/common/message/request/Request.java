package com.github.lbovolini.crowd.common.message.request;

import java.io.Serializable;

public class Request implements Serializable {
    private final String name;
    private final Class<?>[] types;
    private final Object[] args;

    public Request(String name, Class<?>[] types, Object[] args) {
        this.name = name;
        this.types = types;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public Object[] getArgs() {
        return args;
    }
}
