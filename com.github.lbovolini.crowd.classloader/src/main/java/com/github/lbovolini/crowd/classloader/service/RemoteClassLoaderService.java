package com.github.lbovolini.crowd.classloader.service;

import com.github.lbovolini.crowd.classloader.DefaultRemoteClassLoader;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Objects;

public class RemoteClassLoaderService<T extends ClassLoader & RemoteClassLoader> {

    public static final boolean CLASSLOADER_CUSTOM = Boolean.parseBoolean(System.getProperty("classloader.custom", "false"));
    public static final String CLASSLOADER = System.getProperty("classloader", "com.github.lbovolini.crowd.android.classloader.AndroidRemoteClassLoader");

    private final String classPath;
    private final String libPath;

    private final ClassLoader parent;
    private final T customClassLoader;

    public RemoteClassLoaderService(String classPath, String libPath) {
        this.classPath = Objects.requireNonNull(classPath);
        this.libPath = Objects.requireNonNull(libPath);
        this.parent = RemoteClassLoaderService.class.getClassLoader();
        this.customClassLoader = create();
    }

    @SuppressWarnings("unchecked")
    private <T extends ClassLoader & RemoteClassLoader> T create() {

        if (!CLASSLOADER_CUSTOM) {
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
