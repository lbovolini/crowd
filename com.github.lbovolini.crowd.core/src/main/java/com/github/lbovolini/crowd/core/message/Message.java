package com.github.lbovolini.crowd.core.message;

import com.github.lbovolini.crowd.core.object.RemoteObjectInputStream;
import com.github.lbovolini.crowd.core.object.RemoteObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Message {

    private static final int HEADER_SIZE = Byte.BYTES + Short.BYTES;

    private final byte type;
    private final int size;
    private final byte[] data;
    private final short dataLength;

    public static byte getType(ByteBuffer buffer) {
        return buffer.get();
    }

    private Message(byte type, int size, byte[] data, short dataLength) {
        this.type = type;
        this.size = size;
        this.data = data;
        this.dataLength = dataLength;
    }

    public byte getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public byte[] getData() {
        return data;
    }

    public short getDataLength() {
        return dataLength;
    }

    public static Message create(byte type, byte[] data) {
        int size = HEADER_SIZE + data.length;
        return new Message(type, size, data, (short)data.length);
    }

    public static Message create(MessageType type, byte[] data) {
        int size = HEADER_SIZE + data.length;
        return new Message(type.getType(), size, data, (short)data.length);
    }

    public static Message create(MessageType type, Object object) throws IOException {
        byte[] data = serialize(object);
        return create(type, data);
    }

    public static byte[] serialize(Object obj) throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        RemoteObjectOutputStream remoteObjectOutputStream = new RemoteObjectOutputStream(byteArrayOutputStream);

        remoteObjectOutputStream.writeObject(obj);

        remoteObjectOutputStream.close();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        return deserialize(data, Thread.currentThread().getContextClassLoader());
    }

    public static Object deserialize(byte[] data, ClassLoader loader) throws IOException, ClassNotFoundException {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        RemoteObjectInputStream remoteObjectInputStream = new RemoteObjectInputStream(byteArrayInputStream, loader);

        remoteObjectInputStream.close();
        byteArrayInputStream.close();

        return remoteObjectInputStream.readObject();
    }


}
