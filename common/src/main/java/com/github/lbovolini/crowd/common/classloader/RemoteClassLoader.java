package com.github.lbovolini.crowd.common.classloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class RemoteClassLoader extends URLClassLoader {

    public RemoteClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public RemoteClassLoader(String codebase, ClassLoader parent) {
        this(getCodeBase(codebase), parent);
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

    public void addURLs(String codebase) {
        URL[] urls = getCodeBase(codebase);
        addURLs(urls);
    }

    public void addURLs(URL[] urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }

    private static URL[] getCodeBase(String codebase) {
        if (codebase == null || codebase.equals("")) {
            return null;
        }
        String[] strURL = codebase.split(" ");
        URL[] urls = new URL[strURL.length];

        for (int i = 0; i < strURL.length; i++) {
            try {
                urls[i] = new URL(strURL[i]);
            } catch (MalformedURLException e) { e.printStackTrace(); }
        }

        return urls;
    }
}
