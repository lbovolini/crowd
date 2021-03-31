package com.github.lbovolini.crowd.android;

import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.lbovolini.crowd.classloader.RemoteNativeLibrary;
import com.github.lbovolini.crowd.classloader.util.FileDownloader;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

public class AndroidRemoteClassLoader extends DexClassLoader {

    public static final int DEX_MIN_SDK_VERSION = Integer.parseInt(System.getProperty("dex.version", "26"));
    public static final boolean DEX_OPTIMIZE = Boolean.parseBoolean(System.getProperty("dex.optimize", "true"));

    private String classPath;
    private String classPathRoot;
    private URL[] classURLs;
    private ClassLoader parent;

    private static Field pathListField;
    private static Class<?> dexPathListClass;
    private static Constructor<?> dexPathListConstructor;

    private static final DexOptions dexOptions = new DexOptions();
    private static final CfOptions cfOptions = new CfOptions();

    private final RemoteNativeLibrary remoteNativeLibrary;

    static {
        try {
            dexOptions.minSdkVersion = DEX_MIN_SDK_VERSION;
            cfOptions.optimize = DEX_OPTIMIZE;
            dexPathListClass = Class.forName("dalvik.system.DexPathList");
            dexPathListConstructor = dexPathListClass.getConstructor(ClassLoader.class, String.class, String.class, File.class);
            pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
            dexPathListConstructor.setAccessible(true);
            pathListField.setAccessible(true);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public AndroidRemoteClassLoader(URL[] classURLs, URL libURL, String classPath, String libPath, ClassLoader parent){
        super(classPath, null, libPath, parent);
        this.classURLs = classURLs;
        this.classPath = classPath;
        this.classPathRoot = classPath;
        this.parent = parent;
        this.remoteNativeLibrary = new RemoteNativeLibrary(libURL, libPath);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        Class<?> loadedClass = findLoadedClass(name);

        if (loadedClass == null) {
            try {
                loadedClass = parent.loadClass(name);
            } catch (ClassNotFoundException e) {
                loadedClass = downloadClass(name);
            }
        }

        if (resolve) {
            resolveClass(loadedClass);
        }

        return loadedClass;
    }

    @Override
    public String findLibrary(String libName) {
        try {
            return remoteNativeLibrary.download(libName);
        } catch (IOException e) {
            return super.findLibrary(libName);
        }
    }

    public void addURLs(URL[] urls) {

        Set<URL> urlsSet = new HashSet<>(Arrays.asList(this.classURLs));

        urlsSet.addAll(Arrays.asList(urls));
        this.classURLs = (URL[]) urlsSet.toArray();
    }

    public Class<?> downloadClass(String name) throws ClassNotFoundException {

        String urlName = name.replace('.', '/');
        File classFile = new File(String.format("%s/%s.class", classPathRoot, urlName));

        classFile.getParentFile().mkdirs();

        if (classFile.exists()) {
            classFile.delete();
        }

        boolean success = false;

        for (URL baseURL : classURLs) {
            try {
                URL url = getClassURL(baseURL, urlName + ".class");

                if (FileDownloader.download(url, classFile.getAbsolutePath())) {
                    success = true;
                    break;
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        if (!success) {
            return null;
        }

        try {
            byte[] classData = Files.readAllBytes(classFile.toPath());
            return defineClass(name, classData);
        } catch (IOException e) {
            return null;
        }
    }

    public Class<?> defineClass(String name, byte[] bytes) {
        try {
            String path = convert(name, bytes);
            classPath = classPath + ":" + path;
            pathListField.set(this, dexPathListConstructor.newInstance(this, path, null, null));

            return findClass(name);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String convert(String name, byte[] bytes) throws IOException {

        String urlName = name.replace('.', '/');
        File dexFile = new File(String.format("%s/%s.dex", classPathRoot, urlName));

        try (final FileOutputStream fileOutputStream = new FileOutputStream(dexFile)) {
            DirectClassFile cf = new DirectClassFile(bytes, classPathRoot + urlName + ".class", false);
            cf.setAttributeFactory(new StdAttributeFactory());

            DexFile file = new DexFile(dexOptions);
            DxContext context = new DxContext();
            ClassDefItem classDefItem = CfTranslator.translate(context, cf, bytes, cfOptions, dexOptions, file);
            file.add(classDefItem);

            dexFile.getParentFile().mkdirs();

            file.writeTo(fileOutputStream, null, false);

            return dexFile.getAbsolutePath();
        }
    }

    private URL getClassURL(URL baseURL, String relativeClassPath) throws MalformedURLException {

        if (baseURL.toString().endsWith(".jar")) {
            return new URL(baseURL, "jar:" + baseURL.toString() + "!/" + relativeClassPath);
        }

        return new URL(baseURL.toString() + relativeClassPath);
    }
}


