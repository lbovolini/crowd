package com.github.lbovolini.crowd.common.message.request;

import java.io.Serializable;

public class Request implements Serializable {
    private final String name;
    private final Object[] args;

    public Request(String name, Object[] args) {
        this.name = name;
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public Object[] getArgs() {
        return args;
    }
}
