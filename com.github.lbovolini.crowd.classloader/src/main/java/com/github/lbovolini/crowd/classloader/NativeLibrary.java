package com.github.lbovolini.crowd.classloader;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NativeLibrary {

    private static final Map<String, Integer> loadedLibs = new ConcurrentHashMap<>();

    /**
     * Cria o arquivo local onde será salva a biblioteca nativa.
     * @param filePath
     */
    public static void create(String filePath) {
        File file = new File(filePath);
        file.deleteOnExit();
    }

    /**
     * Gera um novo nome para a biblioteca nativa de acordo com a versão atual.
     * @param name
     * @return
     */
    public static String getNewName(String name) {
        int version = loadedLibs.get(name);
        String newName = name + version;
        return System.mapLibraryName(newName);
    }

    /**
     * Atualiza a versão da biblioteca nativa se não é a primeira vez que ela está sendo carregada.
     * @param name
     */
    public static void update(String name) {
        if (isLoaded(name)) {
            updateVersion(name);
        } else { add(name); }
    }

    /**
     * Verifica se a biblioteca nativa já foi carregada anteriormente.
     * @param name
     * @return
     */
    public static boolean isLoaded(String name) {
        return loadedLibs.containsKey(name);
    }

    /**
     * Atualiza a versão da biblioteca nativa.
     * @param name
     */
    public static void updateVersion(String name) {
        loadedLibs.computeIfPresent(name, (key, value) -> value + 1);
    }

    /**
     * Adiciona o nome e a versão inicial (versão 0) da biblioteca ao map de bibliotecas nativa carregadas.
     * @param name
     */
    public static void add(String name) {
        loadedLibs.put(name, 0);
    }
}
