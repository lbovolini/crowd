package com.github.lbovolini.crowd.common.message.request;

import java.io.Serializable;

public class ServiceRequest extends Request implements Serializable {

    public ServiceRequest(String method) {
        super(method, null);
    }
}
