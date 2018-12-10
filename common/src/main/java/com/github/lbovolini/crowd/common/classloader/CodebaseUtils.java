package com.github.lbovolini.crowd.common.classloader;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.lbovolini.crowd.common.configuration.Config.CODEBASE_ROOT;
import static com.github.lbovolini.crowd.common.configuration.Config.CODEBASE_URL;
import static com.github.lbovolini.crowd.common.configuration.Config.LIB_URL;

public class CodebaseUtils {

    public static List<String> getCodebasePaths() {
        List<String> list = new ArrayList<>();
        try {
            Files.walkFileTree(Paths.get(CODEBASE_ROOT), new SimpleFileVisitor<>() {
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
        String url = validURL(CODEBASE_URL);
        List codebasePaths = list.stream().map(e -> url + e).collect(Collectors.toList());
        String codebase = codebasePaths.toString().replace(",", "");
        return removeFLBrackets(codebase);
    }

    public static String getLibURL() {
        return validURL(LIB_URL);
    }

    private static String validURL(String url) {
        if (!url.equals("") && !url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }
}
