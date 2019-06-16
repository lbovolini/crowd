package com.github.lbovolini.example.timeout;

public class Timeout implements RemoteTimeout {

    public Integer say(int i) throws InterruptedException {
        System.out.println("Cliente recebeu " + i);
        Thread.sleep(10000);
        return i++;
    }
}
