package com.github.lbovolini.example.hello;

import com.github.lbovolini.crowd.server.Crowd;

import java.util.concurrent.CompletableFuture;

public class Master {

    static int i = 0;
    static final Object lock = new Object();

    public static void solve(IHello<CompletableFuture<String>> hello) {

        synchronized (lock) {
            if (i > Short.MAX_VALUE) {
                return;
            }
            i++;
        }

        try {
            CompletableFuture<String> result = hello.say(i);

            result.whenComplete((response, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                } else {
                    System.out.println(response);
                    solve(hello);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        Crowd<IHello<CompletableFuture<String>>> crowd = new Crowd<>(Hello.class.getName());
        crowd.forAll(Master::solve);
    }
}
