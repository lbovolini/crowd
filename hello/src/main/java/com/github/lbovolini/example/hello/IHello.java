package com.github.lbovolini.example.hello;

public interface IHello<T> {

    void say();

    T say(int i) throws InterruptedException;
}
