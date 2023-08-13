package com.github.lbovolini.crowd.example.hello;

public class Hello implements IHello<String> {
    int x;

    public String say(Integer i) throws InterruptedException {
        System.out.println(String.valueOf(i).concat(" Hello world"));
        //Thread.sleep(1000);
        String response = String.valueOf(i).concat(" done ").concat(String.valueOf(x++));
        return response;
    }
}
