package com.github.lbovolini.crowd.group.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MulticastMessageType {

    JOIN     ((byte) 1),
    LEAVE    ((byte) 2),
    REPLY    ((byte) 3),
    INVOKE   ((byte) 4),
    CREATE   ((byte) 5),
    SERVICE  ((byte) 6),
    SERVER   ((byte) 7),
    INFO     ((byte) 8),
    UPDATE   ((byte) 9),
    RELOAD   ((byte) 10),
    DISCOVER ((byte) 11),
    CONNECT  ((byte) 12),
    HEARTBEAT((byte) 127);

    private final byte type;

    private static final Map<Byte, MulticastMessageType> messageTypeMap;

    MulticastMessageType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    static {
        Map<Byte, MulticastMessageType> map = new HashMap<>();

        for (MulticastMessageType message : MulticastMessageType.values()) {
            map.put(message.getType(), message);
        }

        messageTypeMap = Collections.unmodifiableMap(map);
    }

    public static MulticastMessageType get(Byte type) {
        return messageTypeMap.get(type);
    }

}


