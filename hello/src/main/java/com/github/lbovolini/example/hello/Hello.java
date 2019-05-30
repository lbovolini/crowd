package com.github.lbovolini.example.hello;

public class Hello implements IHello {

    int cont;

    public void say() {
        System.out.println("Hello world");
    }

    public Integer say(int i) throws InterruptedException {
        System.out.println("Cliente recebeu " + i);
        Thread.sleep(2000);
        return cont++;
    }

}
