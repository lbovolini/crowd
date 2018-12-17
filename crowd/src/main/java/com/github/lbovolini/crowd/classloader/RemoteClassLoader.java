package com.github.lbovolini.crowd.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class RemoteClassLoader extends URLClassLoader {

    public RemoteClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    protected String findLibrary(String libname) {
        return NativeLibrary.loadLib(libname, true);
    }


    public void addURLs(URL[] urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }
}
