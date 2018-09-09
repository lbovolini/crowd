package com.github.lbovolini.crowd.common.classloader;

import java.io.*;

public class RemoteObjectInputStream extends ObjectInputStream {

    private ClassLoader classLoader;

    public RemoteObjectInputStream(InputStream inputStream, ClassLoader classLoader) throws IOException {
        super(inputStream);

        if (classLoader == null) {
            throw new IllegalArgumentException("NULL classLoader");
        }
        this.classLoader = classLoader;
    }

    protected Class resolveClass(ObjectStreamClass classDesc) throws ClassNotFoundException {
        String className = classDesc.getName();
        return Class.forName(className , false , this.classLoader);
    }

}
