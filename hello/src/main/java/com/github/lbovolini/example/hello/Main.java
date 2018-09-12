package com.github.lbovolini.example.hello;

import com.github.lbovolini.crowd.server.node.NodeGroup;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static AtomicInteger cont = new AtomicInteger();

    public static void solve(IHello<CompletableFuture> hello) {

        int i = cont.incrementAndGet();

        if (i > Short.MAX_VALUE) {
            return;
        }

        CompletableFuture<Void> result = hello.say(i);
        result.whenComplete((aVoid, ex) -> {
           if (ex != null) {
               ex.printStackTrace();
           } else {
               solve(hello);
           }
        });

    }

    public static void main(String[] args) {

        NodeGroup nodeGroup = new NodeGroup(Hello.class.getName());
        nodeGroup.forAll((hello, time) -> solve((IHello)hello));
    }

}
