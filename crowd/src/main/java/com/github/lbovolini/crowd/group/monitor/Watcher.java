package com.github.lbovolini.crowd.group.monitor;

import com.github.lbovolini.crowd.configuration.Config;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Watcher {

    private final Path root;
    private final Map<WatchKey, Path> keys = new HashMap<>();
    private final WatcherHandler watcherHandler;
    private WatchService watchService;
    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    public Watcher(String root, WatcherHandler watcherHandler)  {
        this.root = Paths.get(root);
        this.watcherHandler = watcherHandler;
    }

    public void start() {
        pool.execute(monitor());
    }

    private Runnable monitor() {
        return () -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                this.watchService = watchService;
                registerRecursive(root);

                while (true) {
                    handleEvents(watchService.take());
                }
            } catch (IOException | InterruptedException e) { e.printStackTrace(); }
        };
    }

    public void stop() {
        try {
            watchService.close();
            pool.shutdown();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void handleEvents(final WatchKey key) throws IOException {
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            if (kind == StandardWatchEventKinds.OVERFLOW) { continue; }

            String filename = (event.context()).toString();
            Path path = keys.get(key).resolve(filename);

            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                onCreate(path, filename);
            } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                onModify(path, filename);
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                onDelete(path, filename);
            }
        }
        key.reset();
    }

    private void register(final Path path) throws IOException {
        WatchKey watchKey = path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        keys.put(watchKey, path);
    }

    private void registerRecursive(final Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                register(path);
                return super.preVisitDirectory(path, attrs);
            }
        });
    }

    private void onCreate(Path path, String filename) throws IOException {
        if (Files.isDirectory(path)) {
            register(path);
        } else if (isMonitoredExtension(filename)) {
            watcherHandler.onCreate();
        }
    }

    private void onModify(Path path, String filename) {
        if (isMonitoredExtension(filename)) {
            watcherHandler.onModify();
        }
    }

    private void onDelete(Path path, String filename) {
        if (isMonitoredExtension(filename)) {
            watcherHandler.onDelete();
        }
    }

    public boolean isMonitoredExtension(String filename) {
        for (String extension : Config.EXTENSIONS) {
            if (filename.toLowerCase().endsWith(extension.toLowerCase())) { return true; }
        }
        return false;
    }
}
