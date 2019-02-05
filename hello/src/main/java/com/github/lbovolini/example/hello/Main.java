package com.github.lbovolini.example.hello;


import com.github.lbovolini.crowd.node.NodeGroup;

import java.util.concurrent.CompletableFuture;

public class Main {

    static int repeats = 1_000_000;

    public static void solve(IHello<CompletableFuture> hello) {

        long start = System.nanoTime();
        for(int i = 0; i < repeats; i++) {
            hello.say(i);
        }

        System.out.println("TPS : " + repeats / ((System.nanoTime() - start) / 1_000_000_000.0));
    }

    public static void main(String[] args) throws Exception {
        NodeGroup<IHello> nodeGroup = new NodeGroup<>(Hello.class.getName());
        nodeGroup.forOne(hello -> solve(hello));
    }
}
