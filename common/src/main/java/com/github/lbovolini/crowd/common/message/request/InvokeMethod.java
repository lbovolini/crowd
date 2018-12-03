package com.github.lbovolini.crowd.common.message.request;

import java.io.Serializable;
import java.util.UUID;

public class InvokeMethod extends Request implements Serializable {

    private final UUID uuid;

    public InvokeMethod(UUID uuid, String method, Object[] args) {
        super(method, args);
        this.uuid   = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
