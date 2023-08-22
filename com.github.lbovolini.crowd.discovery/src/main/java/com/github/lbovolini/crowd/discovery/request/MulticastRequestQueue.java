package com.github.lbovolini.crowd.discovery.request;

public interface MulticastRequestQueue {

    boolean enqueue(MulticastRequest request);
}
