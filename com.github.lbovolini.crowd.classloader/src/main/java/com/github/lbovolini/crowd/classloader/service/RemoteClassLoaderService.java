package com.github.lbovolini.crowd.classloader.service;

import com.github.lbovolini.crowd.classloader.DefaultRemoteClassLoader;

import java.lang.reflect.Constructor;
import java.net.URL;

public class RemoteClassLoaderService<T extends ClassLoader & RemoteClassLoader> {

    public static final String VENDOR = System.getProperty("java.vendor");
    public static final String CLASSLOADER = System.getProperty("classloader", "com.github.lbovolini.crowd.android.AndroidRemoteClassLoader");

    private final String classPath;
    private final String libPath;

    private final ClassLoader parent;
    private final T customClassLoader;

    public RemoteClassLoaderService(String classPath, String libPath) {
        this.classPath = classPath;
        this.libPath = libPath;
        this.parent = RemoteClassLoaderService.class.getClassLoader();
        this.customClassLoader = create();
    }

    @SuppressWarnings("unchecked")
    private <T extends ClassLoader & RemoteClassLoader> T create() {

        if (VENDOR.contains("Oracle") || VENDOR.equals("N/A")) {
            return (T) new DefaultRemoteClassLoader(new URL[] {}, null, this.classPath, this.libPath, this.parent);
        }

        try {
            Class<?> classDefinition = Class.forName(CLASSLOADER);
            Constructor<?> constructor = classDefinition.getConstructor(URL[].class, URL.class, String.class, String.class, ClassLoader.class);

            return (T) constructor.newInstance(new URL[] {}, null, this.classPath, this.libPath, this.parent);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void addURLs(URL[] classURLs) {
        customClassLoader.addURLs(classURLs);
    }

    public void addLibURL(URL libURL) {
        customClassLoader.addLibURL(libURL);
    }

    public void reload(URL[] classURLs, URL libURL) {

    }

    public T getCustomClassLoader() {
        return customClassLoader;
    }
}
