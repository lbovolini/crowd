package com.github.lbovolini.crowd.common.classloader;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.lbovolini.crowd.common.configuration.Config.EXTENSIONS;
import static java.nio.file.StandardWatchEventKinds.*;

public class Monitor {

    private final Path root;
    private final WatchService watchService;
    private final Map<WatchKey, Path> keys = new HashMap<>();
    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public Monitor(String root) throws IOException {
        this.root = Paths.get(root);
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                watchService.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        pool.execute(() -> {
            try {
                monitor();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void onChange() {
        throw new UnsupportedOperationException();
    }

    private void monitor() throws InterruptedException, IOException {

        registerRecursive(root);

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();
                if (kind == OVERFLOW) {
                    continue;
                }
                Path filename = (Path) event.context();
                Path path = keys.get(key).resolve(filename);

                if (kind == ENTRY_CREATE) {
                    if (Files.isDirectory(path)) {
                        register(path);
                    } else {
                        for (String ext : EXTENSIONS) {
                            if (filename.toString().toLowerCase().endsWith(ext.toLowerCase())) {
                                onChange();
                                break;
                            }
                        }
                    }
                } else if (kind == ENTRY_MODIFY) {
                    for (String ext : EXTENSIONS) {
                        if (filename.toString().toLowerCase().endsWith(ext.toLowerCase())) {
                            onChange();
                            break;
                        }
                    }
                }
            }
            key.reset();
        }
    }

    private void registerRecursive(final Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                WatchKey watchKey = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                keys.put(watchKey, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void register(final Path path) throws IOException {
        WatchKey watchKey = path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(watchKey, path);
    }
}
