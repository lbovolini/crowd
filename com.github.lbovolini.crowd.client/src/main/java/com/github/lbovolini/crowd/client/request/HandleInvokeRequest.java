package com.github.lbovolini.crowd.client.request;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.messages.InvokeMethod;
import com.github.lbovolini.crowd.core.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleInvokeRequest {

    private static final Logger log = LoggerFactory.getLogger(HandleInvokeRequest.class);

    public static InvokeMethod handle(Request request) {
        try {
            return (InvokeMethod) Message.deserialize(request.getMessage().getData());
        } catch (Exception e) {
            log.debug("Error while invoke method", e);
            return null;
        }
    }
}
