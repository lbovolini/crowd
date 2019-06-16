package com.github.lbovolini.example.timeout;

public interface RemoteTimeout<T> {

    T say(int i) throws InterruptedException;
}
