package com.github.lbovolini.crowd.common.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class RemoteClassLoader extends URLClassLoader {

    public RemoteClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
