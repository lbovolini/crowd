package com.github.lbovolini.crowd.example.timeout;

public interface RemoteTimeout<T> {

    T say(int i) throws InterruptedException;
}
