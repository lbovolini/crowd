package com.github.lbovolini.crowd.common.classloader;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.lbovolini.crowd.common.configuration.Config.*;

public class NativeLibrary {

    final static String OS = System.getProperty("os.name").toLowerCase();
    final static Map<String, Integer> loadedLibs = new ConcurrentHashMap<>();

    public static void downloadLib(File destination, String libname) {
        try {
            URL website = new URL(LIB_URL + libname);
            URLConnection connection = website.openConnection();
            ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(destination);
            long expectedSize = connection.getContentLength();

            long transferredSize = 0L;
            while( transferredSize < expectedSize ) {
                transferredSize += fos.getChannel().transferFrom( rbc, transferredSize, 1 << 24 );
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loadLib(String name, boolean reload) {
        //linux
        String libname;
        if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ) {
            if (loadedLibs.containsKey(name)) {
                //! todo
                Integer version = loadedLibs.get(name);
                version++;
                loadedLibs.put(name, version);
            } else {
                loadedLibs.put(name, 0);
            }
            libname = "lib" + name + loadedLibs.get(name) + ".so";
            String filePath = LIB_PATH + "/" + libname;

            if (LIB_PATH.endsWith("/")) {
                filePath = LIB_PATH + libname;
            }

            File file = new File(filePath);
            file.deleteOnExit();
            downloadLib(file, System.mapLibraryName(name));

            return filePath;
        //!todo
        } else {
            System.out.println("todo");
        }
        return null;
    }

}
