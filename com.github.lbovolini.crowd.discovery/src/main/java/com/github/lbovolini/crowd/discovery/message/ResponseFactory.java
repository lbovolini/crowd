package com.github.lbovolini.crowd.discovery.message;

public abstract class ResponseFactory {

    public static final String SEPARATOR = ";";

    public abstract byte[] get(MulticastMessageType type);
}
