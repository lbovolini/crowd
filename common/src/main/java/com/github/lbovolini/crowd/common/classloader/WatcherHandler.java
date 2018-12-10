package com.github.lbovolini.crowd.common.classloader;

public interface WatcherHandler {

    void onCreate();

    void onModify();

    void onDelete();
}
