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

    private static final Map<String, Integer> loadedLibs = new ConcurrentHashMap<>();

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

    /**
     * Cria o arquivo local onde será salva a biblioteca nativa.
     * @param filePath
     */
    private void create(String filePath) {
        File file = new File(filePath);
        file.deleteOnExit();
    }

    /**
     * Gera um novo nome para a biblioteca nativa de acordo com a versão atual.
     * @param name
     * @return
     */
    private String getNewName(String name) {
        int version = loadedLibs.get(name);
        String newName = name + version;
        return System.mapLibraryName(newName);
    }

    /**
     * Atualiza a versão da biblioteca nativa se não é a primeira vez que ela está sendo carregada.
     * @param name
     */
    private void update(String name) {
        if (isLoaded(name)) {
            updateVersion(name);
        } else { add(name); }
    }

    /**
     * Verifica se a biblioteca nativa já foi carregada anteriormente.
     * @param name
     * @return
     */
    private boolean isLoaded(String name) {
        return loadedLibs.containsKey(name);
    }

    /**
     * Atualiza a versão da biblioteca nativa.
     * @param name
     */
    private void updateVersion(String name) {
        loadedLibs.computeIfPresent(name, (key, value) -> value + 1);
    }

    /**
     * Adiciona o nome e a versão inicial (versão 0) da biblioteca ao map de bibliotecas nativa carregadas.
     * @param name
     */
    private void add(String name) {
        loadedLibs.put(name, 0);
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
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        this.libPath = path;
    }
}
