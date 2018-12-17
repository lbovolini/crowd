package com.github.lbovolini.crowd.classloader;

public interface WatcherHandler {

    void onCreate();

    void onModify();

    void onDelete();
}
