package com.github.lbovolini.crowd.common.object;

import com.github.lbovolini.crowd.common.message.Message;

public interface Proxy {
    void enqueue(Message message) throws Exception;
}