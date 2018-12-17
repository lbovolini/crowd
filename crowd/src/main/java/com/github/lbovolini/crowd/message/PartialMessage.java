package com.github.lbovolini.crowd.message;

import java.nio.ByteBuffer;

public class PartialMessage {
    private byte type;
    private short size;
    private byte[] sizeByte;
    private byte[] data;
    private int readSize;
    private final Flags flags;
    private int position;

    public PartialMessage() {
        sizeByte = new byte[2];
        flags = new Flags();
        position = 1;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSizeFirstByte(byte size1) {
        sizeByte[0] = size1;
    }

    public void setSizeLastByte(byte size2) {
        sizeByte[1] = size2;
        size = ByteBuffer.wrap(sizeByte).getShort();
    }

    public void setSize(short size) {
        this.size = size;
    }

    public void allocate() {
        data = new byte[size];
    }

    public int getReadSize() {
        return readSize;
    }

    public byte[] getData() {
        return data;
    }

    public void setReadSize(int readSize) {
        this.readSize = readSize;
    }

    public void read(ByteBuffer buffer, int offset, int length) {
        buffer.get(data, offset, length);
    }

    public Flags getFlags() {
        return flags;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void next() {
        position++;
    }
}