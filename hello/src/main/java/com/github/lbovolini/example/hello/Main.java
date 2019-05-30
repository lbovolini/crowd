package com.github.lbovolini.example.hello;


import com.github.lbovolini.crowd.node.NodeGroup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {

    static int i;

    public static void solve(IHello<CompletableFuture> hello) {

        i++;

        try {
            CompletableFuture<Integer> res = hello.say(i);
            res
                .whenComplete((e, ex) -> {
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    }
                    System.out.println("Servidor recebeu " + e);
                });
        }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws Exception {
        NodeGroup<IHello> nodeGroup = new NodeGroup<>(Hello.class.getName());
        nodeGroup.forOne(hello -> solve(hello));
    }
}
