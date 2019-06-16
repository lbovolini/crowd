package com.github.lbovolini.example.timeout;

import com.github.lbovolini.crowd.node.NodeGroup;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Master {

    public static void solve(RemoteTimeout<CompletableFuture> timeout) {

        try {
            CompletableFuture<Integer> promise = timeout.say(1);

            promise.orTimeout(1, TimeUnit.SECONDS)
                    .whenComplete(Master::handle);

        } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void handle(Integer result, Throwable throwable) {
        System.out.println(result);
    }

    public static void main(String[] args) throws IOException {

        NodeGroup<RemoteTimeout> nodeGroup = new NodeGroup(Timeout.class.getName());
        nodeGroup.forOne(Master::solve);
    }
}
