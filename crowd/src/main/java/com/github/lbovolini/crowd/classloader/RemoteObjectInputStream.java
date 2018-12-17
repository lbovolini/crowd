package com.github.lbovolini.crowd.classloader;

import java.io.*;

// !todo
public class RemoteObjectInputStream extends ObjectInputStream {

    private final ClassLoader classLoader;

    public RemoteObjectInputStream(InputStream inputStream, ClassLoader classLoader) throws IOException {
        super(inputStream);

        if (classLoader == null) {
            throw new IllegalArgumentException("ClassLoader is not defined");
        }
        this.classLoader = classLoader;
    }

    protected Class resolveClass(ObjectStreamClass classDesc) throws ClassNotFoundException {
        String className = classDesc.getName();
        return Class.forName(className , false , this.classLoader);
    }

}
