package com.github.lbovolini.crowd.core.object;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

// !todo
public class RemoteObjectOutputStream extends ObjectOutputStream {
    public RemoteObjectOutputStream(OutputStream out) throws IOException {
        super(out);
    }
}
