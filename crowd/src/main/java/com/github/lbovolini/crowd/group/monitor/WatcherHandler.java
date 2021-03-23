package com.github.lbovolini.crowd.group.monitor;

public interface WatcherHandler {

    void onCreate();

    void onModify();

    void onDelete();
}
