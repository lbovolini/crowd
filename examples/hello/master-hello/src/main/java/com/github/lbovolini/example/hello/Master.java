package com.github.lbovolini.example.hello;

import com.github.lbovolini.crowd.node.NodeGroup;

import java.util.concurrent.CompletableFuture;

public class Master {

    static int i = 0;
    static final Object lock = new Object();

    public static void solve(IHello<CompletableFuture> hello) {

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
        NodeGroup<IHello> nodeGroup = new NodeGroup<>(Hello.class.getName());
        nodeGroup.forAll(hello -> solve(hello));
    }
}
