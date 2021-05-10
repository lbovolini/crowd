package com.github.lbovolini.crowd.example.hello;

public class Hello implements IHello<String> {
    int x;

    public String say(Integer i) throws InterruptedException {
        System.out.println(i + " Hello world");
        Thread.sleep(1000);
        String response = i + " done " + x++;
        return response;
    }
}