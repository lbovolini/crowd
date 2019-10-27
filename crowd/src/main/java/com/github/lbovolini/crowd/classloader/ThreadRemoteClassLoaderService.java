package com.github.lbovolini.crowd.classloader;

import java.lang.reflect.Constructor;
import java.net.URL;

import static com.github.lbovolini.crowd.configuration.Config.CLASSLOADER;
import static com.github.lbovolini.crowd.configuration.Config.VENDOR;

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

    //! TODO
    public void create(URL[] classURLs, URL libURL) {
        try {
            if (VENDOR.contains("Oracle") || VENDOR.equals("N/A")) {
                classLoader = new RemoteClassLoader(classURLs, libURL, this.classPath, this.libPath, this.parent);
            }
            else {
                Class classDefinition = Class.forName(CLASSLOADER);
                Constructor constructor = classDefinition.getConstructor(URL[].class, URL.class, String.class, String.class, ClassLoader.class);
                classLoader = (ClassLoader) constructor.newInstance(classURLs, libURL, this.classPath, this.libPath, this.parent);
            }
            thread.setContextClassLoader(classLoader);
        } catch (Exception e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }
}