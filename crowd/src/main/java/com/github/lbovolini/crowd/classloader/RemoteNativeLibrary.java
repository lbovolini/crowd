package com.github.lbovolini.crowd.classloader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteNativeLibrary {

    private URL libURL;
    private String libPath;

    private static final Map<String, Integer> loadedLibs = new ConcurrentHashMap<>();

    public RemoteNativeLibrary(URL url, String path) {
        setUrl(url);
        setPath(path);
    }

    public String download(String name) throws IOException {

        update(name);

        String newName = getNewName(name);
        String filePath = this.libPath + newName;

        create(filePath);
        String remoteLibName = System.mapLibraryName(name);
        if (!FileDownloader.download(this.libURL, filePath, remoteLibName)) {
            throw new IOException(String.format("Native Library %s not found", name));
        }

        return filePath;
    }

    private void create(String filePath) {
        File file = new File(filePath);
        file.deleteOnExit();
    }

    private String getNewName(String name) {
        int version = loadedLibs.get(name);
        String newName = name + version;
        return System.mapLibraryName(newName);
    }

    private void update(String name) {
        if (isLoaded(name)) {
            updateVersion(name);
        } else { add(name); }
    }

    private boolean isLoaded(String name) {
        return loadedLibs.containsKey(name);
    }

    private void updateVersion(String name) {
        loadedLibs.computeIfPresent(name, (key, value) -> value + 1);
    }

    private void add(String name) {
        loadedLibs.put(name, 0);
    }

    public void setUrl(URL url) {
        try {
            if(!url.toString().endsWith("/")) {
                url = new URL(url + "/");
            }
            this.libURL = url;
        } catch (MalformedURLException e) { throw new IllegalArgumentException(url.toString()); }
    }

    private void setPath(String path) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        this.libPath = path;
    }
}
