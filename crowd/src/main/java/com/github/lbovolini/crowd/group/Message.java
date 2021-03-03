package com.github.lbovolini.crowd.group;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class Message {

    private final byte[] data;
    private final int dataLength;
    private final InetSocketAddress address;

    public Message(byte[] data, int dataLength, InetSocketAddress address) {
        this.data = data;
        this.dataLength = dataLength;
        this.address = address;
    }

    public Message(byte[] data, int dataLength, SocketAddress address) {
        this(data, dataLength, (InetSocketAddress) address);
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsString() {
        return new String(data, 0, dataLength, StandardCharsets.UTF_8);
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public int getDataLength() {
        return dataLength;
    }

    public static Message ofType(String type, InetSocketAddress address) {
        byte[] data = ResponseFactory.get(type);
        return new Message(data, data.length, address);
    }
}
