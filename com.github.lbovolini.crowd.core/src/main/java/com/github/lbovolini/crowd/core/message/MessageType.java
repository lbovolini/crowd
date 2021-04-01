package com.github.lbovolini.crowd.core.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MessageType {
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

    private static final Map<Byte, MessageType> messageTypeMap;

    MessageType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    static {
        Map<Byte, MessageType> map = new HashMap<>();

        for (MessageType messageType : MessageType.values()) {
            map.put(messageType.getType(), messageType);
        }

        messageTypeMap = Collections.unmodifiableMap(map);
    }

    public static MessageType get(Byte type) {
        return messageTypeMap.get(type);
    }
}
