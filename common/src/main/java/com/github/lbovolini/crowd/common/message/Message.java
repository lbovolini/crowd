package com.github.lbovolini.crowd.common.message;

import com.github.lbovolini.crowd.common.classloader.RemoteObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Message {

    public static final int HEADER_SIZE = Byte.BYTES + Short.BYTES;

    private final byte type;
    private final int size;
    private final byte[] data;
    private final short dataLength;

    public enum Type {
        JOIN((byte)1),
        LEAVE((byte)2),
        REPLY((byte)3),
        INVOKE((byte)4);

        byte type;

        private static final Map<Byte,Type> ENUM_MAP;

        Type(byte type) {
            this.type = type;
        }

        public byte getType() {
            return type;
        }

        static {
            Map<Byte, Type> map = new ConcurrentHashMap<>();

            for (Type message : Type.values()) {
                map.put(message.getType(), message);
            }

            ENUM_MAP = Collections.unmodifiableMap(map);
        }

        public static Type get(Byte type) {
            return ENUM_MAP.get(type);
        }
    }

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

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        return deserialize(data, Thread.currentThread().getContextClassLoader());
    }

    public static Object deserialize(byte[] data,  ClassLoader loader) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        RemoteObjectInputStream is = new RemoteObjectInputStream(in, loader);
        return is.readObject();
    }

}
