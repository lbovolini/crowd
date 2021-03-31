package com.github.lbovolini.crowd.classloader;

import com.github.lbovolini.crowd.classloader.service.RemoteClassLoader;
import com.github.lbovolini.crowd.classloader.service.RemoteClassLoaderService;

import java.net.URL;
import java.util.concurrent.ThreadFactory;

public class ClassLoaderContext {

    private final String classPath;
    private final String libPath;

    private URL[] classURLs;
    private URL libURL;

    private final ClassLoader defaultClassLoader;
    private final RemoteClassLoaderService<? extends RemoteClassLoader> remoteClassLoaderService;

    public ClassLoaderContext(String classPath, String libPath) {
        this.classPath = classPath;
        this.libPath = libPath;
        this.defaultClassLoader = ClassLoaderContext.class.getClassLoader();
        this.remoteClassLoaderService = new RemoteClassLoaderService<>(classPath, libPath);
    }

    public ThreadFactory getThreadFactory() {
        return runnable -> {
            Thread thread = new Thread(runnable);
            thread.setContextClassLoader(remoteClassLoaderService.getCustomClassLoader());

            return thread;
        };
    }

    public String getClassPath() {
        return classPath;
    }

    public String getLibPath() {
        return libPath;
    }

    public URL[] getClassURLs() {
        return classURLs;
    }

    public void setClassURLs(URL[] classURLs) {
        this.classURLs = classURLs;
        remoteClassLoaderService.addURLs(classURLs);
    }

    public URL getLibURL() {
        return libURL;
    }

    public void setLibURL(URL libURL) {
        this.libURL = libURL;
        remoteClassLoaderService.addLibURL(libURL);
    }

    public ClassLoader getDefaultClassLoader() {
        return defaultClassLoader;
    }

}
