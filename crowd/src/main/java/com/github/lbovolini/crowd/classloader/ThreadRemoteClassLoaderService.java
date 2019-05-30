package com.github.lbovolini.crowd.classloader;

import java.net.URL;

public class ThreadRemoteClassLoaderService {

    private final Thread thread;

    private final String classPath;
    private final String libraryPath;

    private final ClassLoader parent;
    private ClassLoader classLoader;

    public ThreadRemoteClassLoaderService(Thread thread, String classPath, String libraryPath) {
        this.thread = thread;
        this.classPath = classPath;
        this.libraryPath = libraryPath;
        this.parent = ThreadRemoteClassLoaderService.class.getClassLoader();
    }

    public void create(URL[] classURLs, URL libURL) {
        classLoader = new RemoteClassLoader(classURLs, this.parent);
        thread.setContextClassLoader(classLoader);
    }
}