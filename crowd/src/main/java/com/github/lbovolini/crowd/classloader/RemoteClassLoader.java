package com.github.lbovolini.crowd.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class RemoteClassLoader extends URLClassLoader {

    private final RemoteNativeLibrary remoteNativeLibrary;

    public RemoteClassLoader(URL[] classURLs, URL libURL, String classPath, String libPath, ClassLoader parent) {
        super(classURLs, parent);
        this.remoteNativeLibrary = new RemoteNativeLibrary(libURL, libPath);
    }

    @Override
    protected String findLibrary(String libraryName) {
        try {
            return remoteNativeLibrary.download(libraryName);
        } catch (IOException e) {
            return null;
        }
    }

    public void addURLs(URL[] urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }
}
