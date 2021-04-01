package com.github.lbovolini.crowd.core.test.object;

public class HelloWorld implements Greetings<Void> {

    private final String message;

    public HelloWorld() {
        message = "Hello world";
    }

    public HelloWorld(String message) {
        this.message = message;
    }

    @Override
    public void hello() {
        System.out.println(message);
    }

    @Override
    public Void hi() {
        System.out.println("HI");
        return null;
    }
}
