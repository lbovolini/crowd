package com.github.lbovolini.crowd.classloader;

import com.github.lbovolini.crowd.classloader.util.FileDownloader;
import com.github.lbovolini.crowd.classloader.util.OsUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Permite o carregamento remoto de bibliotecas nativas.
 */
public class RemoteNativeLibrary {

    private URL libURL;
    private String libPath;

    public RemoteNativeLibrary(URL url, String path) {
        setUrl(url);
        setPath(path);
    }

    /**
     * Faz o download da biblioteca.
     * @param name
     * @return
     * @throws IOException
     */
    public String download(String name) throws IOException {

        NativeLibrary.update(name);

        String newName = NativeLibrary.getNewName(name);
        String filePath = this.libPath + newName;

        NativeLibrary.create(filePath);
        String remoteLibName = System.mapLibraryName(name);

        if (!FileDownloader.download(this.libURL, filePath, remoteLibName)) {
            throw new IOException(String.format("Native Library %s not found", name));
        }

        return filePath;
    }

    /**
     * Atribui a URL da biblioteca remota de acordo com a arquitetura e sistema operacional do dispositivo em execução.
     * @param url
     */
    public void setUrl(URL url) {

        if (Objects.isNull(url)) {
            return;
        }

        String os = OsUtils.getOs();
        String arch = OsUtils.getArch();

        try {
            if(!url.toString().endsWith("/")) {
                url = new URL(url + "/" + os + "/" + arch + "/");
            }
            this.libURL = new URL(url + os + "/" + arch + "/");
        } catch (MalformedURLException e) { throw new IllegalArgumentException(url.toString()); }
    }

    private void setPath(String path) {

        Objects.requireNonNull(path);

        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        this.libPath = path;
    }
}
