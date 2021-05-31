package com.github.lbovolini.crowd.core.test.message;

import java.io.Serializable;

public class MyObject implements Serializable {

    final int a;

    public MyObject(int a) {
        this.a = a;
    }

    public void say() {
        System.out.println("Hello");
    }
}