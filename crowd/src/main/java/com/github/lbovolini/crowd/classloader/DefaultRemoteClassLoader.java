package com.github.lbovolini.crowd.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Responsável por carregar as classes (.class) e as bibliotecas nativas através da rede.
 */
public class DefaultRemoteClassLoader extends URLClassLoader implements RemoteClassLoader {

    private final RemoteNativeLibrary remoteNativeLibrary;

    public DefaultRemoteClassLoader(URL[] classURLs, URL libURL, String classPath, String libPath, ClassLoader parent) {
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

    @Override
    public void addURLs(URL[] urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }

    @Override
    public void addLibURL(URL url) {

    }
}