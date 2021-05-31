package com.github.lbovolini.crowd.core.message.messages;

import java.io.Serializable;
import java.util.Objects;

public class JoinGroup implements Serializable {

    private final int cores;

    public JoinGroup(int cores) {
        this.cores = cores;
    }

    public int getCores() {
        return cores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JoinGroup joinGroup = (JoinGroup) o;

        return cores == joinGroup.cores;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cores);
    }
}
