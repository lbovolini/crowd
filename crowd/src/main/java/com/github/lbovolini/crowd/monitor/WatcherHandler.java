package com.github.lbovolini.crowd.monitor;

public interface WatcherHandler {

    void onCreate();

    void onModify();

    void onDelete();
}
