package com.github.lbovolini.crowd.common.classloader;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

// http://www.szegedi.org/articles/remotejars.html

public class RemoteClassLoader extends URLClassLoader {

    boolean reload = false;
    ClassLoader parent;
    static URL[] urls;
    static Set<String> remoteClasses = new HashSet<>();

    public RemoteClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.urls = urls;
        this.parent = parent;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return parent.loadClass(name);
        } catch (ClassNotFoundException e) {
            remoteClasses.add(name);
            return findClassz(name);
        }
    }

    public Class<?> findClassz(String name) throws ClassNotFoundException {

//        for (URL url : urls) {
//            try {
//
//                String stringURL = url.toString();
//                if (!stringURL.endsWith(".jar")) {
//                    stringURL = stringURL + name;
//                    URL myUrl = new URL(stringURL);
//                    URLConnection connection = myUrl.openConnection();
//                    InputStream input = connection.getInputStream();
//                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//                    int data = input.read();
//
//                    while (data != -1) {
//                        buffer.write(data);
//                        data = input.read();
//                    }
//
//                    input.close();
//
//                    byte[] classData = buffer.toByteArray();
//
//                    return defineClass(name, classData, 0, classData.length);
//                }
//                URL myUrl = new URL("jar", "", stringURL + "!/" + name);
//                JarURLConnection connection = (JarURLConnection)myUrl.openConnection();
//
//
//            } catch (MalformedURLException e) {
//               // e.printStackTrace();
//            } catch (IOException e) {
//                //e.printStackTrace();
//            }
//
//        }
        return super.loadClass(name);
    }

}
