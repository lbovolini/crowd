package com.github.lbovolini.example.hello;

public class Hello implements IHello {

    public Void say(Integer i) {

        System.out.println(i + " Hello world");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
