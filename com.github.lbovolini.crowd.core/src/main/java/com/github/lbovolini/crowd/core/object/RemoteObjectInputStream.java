package com.github.lbovolini.crowd.core.object;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// !todo
public class RemoteObjectInputStream extends ObjectInputStream {

    private final ClassLoader classLoader;

    private static Map<String, Class> PRIMITIVE_MAP;

    static {
        Map<String, Class> map = new HashMap<>();

        map.put("boolean", Boolean.TYPE);
        map.put("byte", Byte.class);
        map.put("char", Character.TYPE);
        map.put("short", Short.TYPE);
        map.put("int", Integer.TYPE);
        map.put("long", Long.TYPE);
        map.put("float", Float.TYPE);
        map.put("double", Double.TYPE);

        PRIMITIVE_MAP = Collections.unmodifiableMap(map);
    }

    public RemoteObjectInputStream(InputStream inputStream, ClassLoader classLoader) throws IOException {
        super(inputStream);
        this.classLoader = classLoader;
    }

    protected Class resolveClass(ObjectStreamClass classDesc) throws ClassNotFoundException {

        String className = classDesc.getName();
        Class primitive = PRIMITIVE_MAP.get(className);

        if (primitive != null) {
            return primitive;
        }

        return Class.forName(className , false , this.classLoader);
    }

}
