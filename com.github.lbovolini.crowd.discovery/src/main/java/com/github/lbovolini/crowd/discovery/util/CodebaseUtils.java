package com.github.lbovolini.crowd.discovery.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CodebaseUtils {

    public static final String LIB_URL = System.getProperty("lib.url", "file:");
    public static final String EXTENSIONS[] = {".class", ".jar", ".so", ".dll"};
    public static final String CODEBASE_ROOT = System.getProperty("codebase.root", getTempDirPath());
    public static final String CODEBASE_URL = System.getProperty("codebase.url", "file:");

    private CodebaseUtils() {}

    // !TODO windows
    public static List<String> getCodebasePaths() {

        final String codebaseRootPath = validateCodebaseRootPath(CODEBASE_ROOT);
        List<String> codebasePathList = new ArrayList<>();

        try {
            Files.walkFileTree(Paths.get(codebaseRootPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".jar")) {
                        if (!file.toAbsolutePath().toString().contains(File.separatorChar + ".")) {
                            codebasePathList.add(file.toString().replaceFirst(codebaseRootPath, ""));
                        }
                    }
                    return super.visitFile(file, attrs);
                }
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!dir.toAbsolutePath().toString().contains("/.")) {
                        String path = dir.toString();
                        if (!path.equals("")) {
                            path = path + "/";
                        }
                        codebasePathList.add(path.replaceFirst(codebaseRootPath, ""));
                    }
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return codebasePathList;
    }

    public static String removeFLBrackets(String string) {
        int length = string.length();
        return string.substring(1, length - 1);
    }

    public static String getCodebaseURLs() {
        List<String> list = getCodebasePaths();
        List<String> codebasePaths = list.stream().map(e -> validURL(CODEBASE_URL) + e).collect(Collectors.toList());
        String codebase = codebasePaths.toString().replace(",", "");
        return removeFLBrackets(codebase);
    }

    private static String validURL(String url) {
        if (!url.endsWith("/") && !url.endsWith(":") && !url.equals("")) {
            return url + "/";
        }
        return url;
    }

    public static String getLibURL() {
        return validURL(LIB_URL);
    }


    private static String validateCodebaseRootPath(final String codebaseRootPath) {

        if (Objects.isNull(codebaseRootPath)) {
            throw new IllegalArgumentException("Codebase root path cannot be null");
        }

        if (!new File(codebaseRootPath).isAbsolute()) {
            throw new IllegalArgumentException("Codebase root path must be absolute");
        }

        if (!new File(codebaseRootPath).isDirectory()) {
            throw new IllegalArgumentException("Codebase root path must refer to a valid local directory");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(codebaseRootPath.charAt(0));
        Character character;

        for (int i = 1; i < codebaseRootPath.length(); i++) {
            character = codebaseRootPath.charAt(i - 1);
            if (character.equals(File.separatorChar)
                    && Character.valueOf(codebaseRootPath.charAt(i)).equals(File.separatorChar)) {
                continue;
            }
            stringBuilder.append(codebaseRootPath.charAt(i));
        }

        if (!stringBuilder.toString().endsWith(File.separator)) {
            stringBuilder.append(File.separatorChar);
        }

        return stringBuilder.toString();
    }

    private static String getTempDirPath() {
        try {
            return Files.createTempDirectory(null).toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
