package com.github.lbovolini.crowd.example.hello;

public interface IHello<T> {
    T say(Integer i) throws InterruptedException ;
}
