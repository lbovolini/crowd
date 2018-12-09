package com.github.lbovolini.crowd.common.classloader;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.lbovolini.crowd.common.configuration.Config.CODEBASE_ROOT;
import static com.github.lbovolini.crowd.common.configuration.Config.CODEBASE_URL;

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
                        list.add(dir.toString());
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
        List codebasePaths = list.stream().map(e -> CODEBASE_URL + e).collect(Collectors.toList());
        String codebase = codebasePaths.toString().replace(",", "");
        return removeFLBrackets(codebase);
    }
}
