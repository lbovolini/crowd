package com.github.lbovolini.example.hello;

import com.github.lbovolini.crowd.node.NodeGroup;

import java.util.concurrent.CompletableFuture;

public class Main {

    static int cont = 0;
    static final Object lock = new Object();

    public static void solve(IHello<CompletableFuture> hello) {

        int i;

        synchronized (lock) {
            if (cont > Short.MAX_VALUE) {
                return;
            }
            i = cont++;
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
        NodeGroup nodeGroup = new NodeGroup(Hello.class.getName());
        nodeGroup.forAll((hello, time) -> solve((IHello)hello));
    }
}
