package com.github.lbovolini.crowd.classloader;


import java.net.MalformedURLException;
import java.net.URL;

public class ThreadRemoteClassLoaderService {

    private final ClassLoader parent;
    private RemoteClassLoader classLoader;
    private final Object lock = new Object();
    private URL[] codebase;

    private final Thread thread;

    public ThreadRemoteClassLoaderService(Thread thread) {
        this.thread = thread;
        this.parent = ThreadRemoteClassLoaderService.class.getClassLoader();
    }


    public void newClassLoader(String codebase) {
        newClassLoader(toURLArray(codebase));
    }

    public void newClassLoader(URL[] codebase) {
        setCodebase(codebase);
        classLoader = new RemoteClassLoader(codebase, parent);
        thread.setContextClassLoader(classLoader);
    }


    public RemoteClassLoader getClassLoader() {
        return classLoader;
    }

    private URL[] getCodebase() {
        synchronized (lock) {
            return this.codebase;
        }
    }

    public void setCodebase(URL[] codebase) {
        synchronized (lock) {
            this.codebase = codebase;
        }
    }


    private static URL[] toURLArray(String codebase) {
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


    public void updateCodebaseURLs(String codebase) {
        updateCodebaseURLs(toURLArray(codebase));
    }

    public void updateCodebaseURLs(URL[] codebase) {
        classLoader.addURLs(codebase);
    }

    public void reload(String codebase) {
        newClassLoader(codebase);
    }
}