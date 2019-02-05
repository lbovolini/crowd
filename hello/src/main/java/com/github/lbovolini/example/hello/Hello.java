package com.github.lbovolini.example.hello;

public class Hello implements IHello {

    public void say() {
        System.out.println("Hello world");
    }

    public void say(int i) {
        System.out.println(i);
    }

}
