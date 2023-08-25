package com.github.lbovolini.crowd.discovery.message;

public interface ResponseFactory {

    String SEPARATOR = ";";

    int MAX_PACKET_SIZE = 65535;

    byte[] get(MulticastMessageType type);
}
