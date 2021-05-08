package com.github.lbovolini.crowd.discovery.message;

public class ClientResponseFactory extends ResponseFactory {

    public byte[] get(MulticastMessageType type) {

        if (type.equals(MulticastMessageType.HEARTBEAT)) {
            return new byte[] { MulticastMessageType.HEARTBEAT.getType() };
        }
        if (type.equals(MulticastMessageType.DISCOVER)) {
            return new byte[] { MulticastMessageType.DISCOVER.getType() };
        }
        return new byte[] {};
    }

}
