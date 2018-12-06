package com.github.lbovolini.example.hello;

public class Hello implements IHello {

    public String say(Integer i) throws InterruptedException {
        System.out.println(i + " Hello world");
        Thread.sleep(1000);
        String response = i + " done";
        return response;
    }
}
