package com.github.lbovolini.crowd.common.classloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteClassLoader extends URLClassLoader {

    private boolean reload = false;
    private final ClassLoader parent;
    private final Object lock = new Object();
    private static final Map<String, UUID> remoteClasses = new ConcurrentHashMap<>();
    private static final UUID first = UUID.randomUUID();

    public RemoteClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.parent = parent;
    }

    public RemoteClassLoader(String codebase, ClassLoader parent) {
        this(getCodeBase(codebase), parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return parent.loadClass(name);
        } catch (Exception e) {
            return super.loadClass(name);
        }
    }

    @Override
    protected String findLibrary(String libname) {
        return NativeLibrary.loadLib(libname, true);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    public Class<?> findClassz(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    protected void addURLs(URL[] urls) {
        for (URL url : urls) {
            super.addURL(url);
        }
    }

//    private Class<?> reloadOne(String name) {
//
//        URL[] urls = getURLs();
//
//
//
//            for (URL url : urls) {
//                String classEntry = getClassEntry(name);
//                try {
//
//                    URL codebase;
//                    if (!url.toString().endsWith("/")) {
//                        codebase = new URL(url, "jar:" + url + "!/" + classEntry);
//                        URLConnection connection = myUrl.openConnection();
//
//                    } else {
//                        codebase = new URL(url, url + classEntry);
//                    }
//                    System.out.println(codebase);
//
//                    final JarURLConnection conn = (JarURLConnection) codebase.openConnection();
//                    final JarFile jarFile = conn.getJarFile();
//                    final Enumeration<JarEntry> entries = jarFile.entries();
//
//
//                    while (entries.hasMoreElements()) {
//                        final JarEntry jarEntry = entries.nextElement();
//
//                        if (!jarEntry.getName().endsWith(".class")) {
//                            continue;
//                        }
//                        if (!getClassName(jarEntry.getName()).equals(name)) {
//                            continue;
//                        }
//                        InputStream input = jarFile.getInputStream(jarEntry);
//                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//                        int data = input.read();
//
//                        while (data != -1) {
//                            buffer.write(data);
//                            data = input.read();
//                        }
//
//                        input.close();
//                        byte[] classData = buffer.toByteArray();
//
//                        return defineClass(name, classData, 0, classData.length);
//                    }
//                } catch (IOException e) {
//                    System.out.println("EX");;
//                }
//            }
//        return null;
//    }
//
//
//    public void reloadAll() throws IOException {
//
//        URL[] urls = getURLs();
//
//
//        for (URL url : urls) {
//            URL codebase = new URL(url, "jar:" + url + "!/");
//
//            final JarURLConnection conn = (JarURLConnection) codebase.openConnection();
//            final JarFile jarFile = conn.getJarFile();
//            final Enumeration<JarEntry> entries = jarFile.entries();
//
//            UUID uuid = UUID.randomUUID();
//            remoteClasses.keySet().forEach(className -> {
//                if (remoteClasses.get(className).equals(uuid)) {
//                    return;
//                }
//                while (entries.hasMoreElements()) {
//                    final JarEntry jarEntry = entries.nextElement();
//
//                    if (!jarEntry.getName().endsWith(".class")) {
//                        continue;
//                    }
//                    String classNameEntry = getClassName(jarEntry.getName());
//                    if (remoteClasses.keySet().contains(classNameEntry)) {
//                        if (remoteClasses.get(className).equals(uuid)) {
//                            continue;
//                        }
//                        remoteClasses.put(classNameEntry, uuid);
//                    } else {
//                        continue;
//                    }
//
//                    byte[] classData = null;
//                    try {
//                        InputStream input = jarFile.getInputStream(jarEntry);
//                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//                        int data = input.read();
//
//                        while (data != -1) {
//                            buffer.write(data);
//                            data = input.read();
//                        }
//
//                        input.close();
//                        classData = buffer.toByteArray();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    System.out.println("REDEFINED " + classNameEntry);
//                    defineClass(classNameEntry, classData, 0, classData.length);
//                }
//            });
//        }
//    }

//    public void mutate(URL[] newUrls) {
//
//        synchronized (lock) {
//            if (hasChanged(newUrls)) {
//                addURLs(newUrls);
//            }
//            try {
//                reloadAll();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void mutate(String codebase) {
//        URL[] newUrls = getCodeBase(codebase);
//        mutate(newUrls);
//    }

    private boolean hasChanged(URL[] newUrls) {
        URL[] oldUrls = getURLs();
        return Arrays.equals(newUrls, oldUrls);
    }


    private static URL[] getCodeBase(String codebase) {
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

    private static String getClassName(String entry) {
        String res = entry.replace("/", ".");
        return res.substring(0, res.length() - 6);
    }

    private static String getClassEntry(String name) {
        String res = name.replace(".", "/");
        return res + ".class";
    }

}
