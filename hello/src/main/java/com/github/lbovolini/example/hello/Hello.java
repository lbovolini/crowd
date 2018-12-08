package com.github.lbovolini.example.hello;

public class Hello implements IHello {
    int x;

    public String say(Integer i) throws InterruptedException {
        System.out.println(i + " Hello world");
        Thread.sleep(10000);
        String response = i + " done " + x++;
        return response;
    }
}
