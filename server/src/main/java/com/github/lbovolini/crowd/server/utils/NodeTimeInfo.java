package com.github.lbovolini.crowd.server.utils;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NodeTimeInfo {
    final int cores;
    final long startTime = System.nanoTime();
    AtomicLong computationTime = new AtomicLong();
    AtomicInteger coresFinished = new AtomicInteger();

    public NodeTimeInfo(int cores) {
        this.cores = cores;
    }

    public boolean allCoresFinished() {
        int finished = coresFinished.incrementAndGet();

        if (cores == finished) {
            return true;
        }
        return false;
    }

    public void addComputationTime(long computationTime) {
        this.computationTime.addAndGet(computationTime);
    }

    public void showTimes() {
        long total = System.nanoTime() - startTime;
        System.out.println("Total: " + (double)total / 1000000000);
        double comp = (double)computationTime.get() / cores;
        System.out.println("Computation: " + comp / 1000000000);
        System.out.println("Loss: " + (total - comp) / 1000000000);
        System.out.println("T: " + comp / total);
        System.out.println();
    }

    public void finished() {
        if (allCoresFinished()) {
            showTimes();
        }
    }

}
