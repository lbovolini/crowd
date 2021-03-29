package com.github.lbovolini.crowd.discovery.message;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MulticastMessage {

    private final byte[] data;
    private final int dataLength;
    private final InetSocketAddress address;

    public MulticastMessage(byte[] data, int dataLength, InetSocketAddress address) {
        this.data = data;
        this.dataLength = dataLength;
        this.address = address;
    }

    public MulticastMessage(byte[] data, int dataLength, SocketAddress address) {
        this(data, dataLength, (InetSocketAddress) address);
    }

    public byte getType() {
        return Objects.requireNonNull(data)[0];
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

    public static MulticastMessage ofType(MulticastMessageType type, InetSocketAddress address) {
        byte[] data = ResponseFactory.get(type);
        return new MulticastMessage(data, data.length, address);
    }
}
