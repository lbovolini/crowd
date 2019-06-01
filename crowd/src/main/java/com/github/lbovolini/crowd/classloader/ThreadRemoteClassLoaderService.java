package com.github.lbovolini.crowd.classloader;

import java.net.URL;

public class ThreadRemoteClassLoaderService {

    private final Thread thread;

    private final String classPath;
    private final String libPath;

    private final ClassLoader parent;
    private ClassLoader classLoader;

    public ThreadRemoteClassLoaderService(Thread thread, String classPath, String libPath) {
        this.thread = thread;
        this.classPath = classPath;
        this.libPath = libPath;
        this.parent = ThreadRemoteClassLoaderService.class.getClassLoader();
    }

    public void create(URL[] classURLs, URL libURL) {
        classLoader = new RemoteClassLoader(classURLs, libURL, this.classPath, this.libPath, this.parent);
        thread.setContextClassLoader(classLoader);
    }
}