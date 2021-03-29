package com.github.lbovolini.crowd.discovery.monitor;

public interface WatcherHandler {

    void onCreate();

    void onModify();

    void onDelete();
}
