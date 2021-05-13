package com.github.lbovolini.crowd.example.timeout.master;

import com.github.lbovolini.crowd.server.Crowd;
import com.github.lbovolini.crowd.example.timeout.RemoteTimeout;
import com.github.lbovolini.crowd.example.timeout.Timeout;

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
        if (throwable != null) {
            throwable.printStackTrace();
            return;
        }
        System.out.println(result);
    }

    public static void main(String[] args) throws IOException {

        Crowd<RemoteTimeout> crowd = new Crowd<>(Timeout.class.getName());
        crowd.forOne(Master::solve);
    }
}
