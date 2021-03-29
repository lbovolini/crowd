package com.github.lbovolini.crowd.discovery.message;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CodebaseUtils {

    public static final String LIB_URL = System.getProperty("lib.url", "file:");
    public static final String EXTENSIONS[] = {".class", ".jar", ".so", ".dll"};
    public static final String CODEBASE_ROOT = System.getProperty("codebase.root", "");
    public static final String CODEBASE_URL = System.getProperty("codebase.url", "file:");

    private CodebaseUtils() {}

    public static List<String> getCodebasePaths() {
        List<String> list = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(CODEBASE_ROOT), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".jar")) {
                        if (!file.toAbsolutePath().toString().contains("/.")) {
                            list.add(file.toString());
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
                        list.add(path);
                    }
                    return super.preVisitDirectory(dir, attrs);
                }
            } );
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public static String removeFLBrackets(String string) {
        int length = string.length();
        return string.substring(1, length - 1);
    }

    public static String getCodebaseURLs() {
        List<String> list = getCodebasePaths();
        List codebasePaths = list.stream().map(e -> validURL(CODEBASE_URL) + e).collect(Collectors.toList());
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
}
