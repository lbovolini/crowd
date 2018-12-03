package com.github.lbovolini.crowd.common.message.request;

import java.io.Serializable;

public class CreateObject extends Request implements Serializable {
    public CreateObject(String name, Object[] args) {
        super(name, args);
    }
}
