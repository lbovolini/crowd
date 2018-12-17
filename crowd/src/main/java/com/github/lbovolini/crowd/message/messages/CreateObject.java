package com.github.lbovolini.crowd.message.messages;

import java.io.Serializable;

public class CreateObject implements Serializable {
    private final String name;
    private final Object[] args;

    public CreateObject(String name, Object[] args) {
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
