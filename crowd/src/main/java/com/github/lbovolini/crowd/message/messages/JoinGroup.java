package com.github.lbovolini.crowd.message.messages;

import java.io.Serializable;

public class JoinGroup implements Serializable {
    private final int cores;

    public JoinGroup(int cores) {
        this.cores = cores;
    }

    public int getCores() {
        return cores;
    }
}
