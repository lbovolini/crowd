package com.github.lbovolini.crowd.discovery.message;

public interface ResponseFactory {

    String SEPARATOR = ";";

    byte[] get(MulticastMessageType type);
}
