package com.github.lbovolini.crowd.core.message;

import java.util.Objects;

public class Flags {

    private boolean hasType;
    private boolean hasSize;
    private boolean hasSizeChunk;
    private boolean hasMessage;
    private boolean hasMessageChunk;

    public void resetAll() {
        hasType = false;
        hasSize = false;
        hasSizeChunk = false;
        hasMessage = false;
        hasMessageChunk = false;
    }

    public boolean hasType() {
        return hasType;
    }

    public void setHasType(boolean hasType) {
        this.hasType = hasType;
    }

    public boolean hasSize() {
        return hasSize;
    }

    public void setHasSize(boolean hasSize) {
        this.hasSize = hasSize;
    }

    public boolean hasSizeChunk() {
        return hasSizeChunk;
    }

    public void setHasSizeChunk(boolean hasSizeChunk) {
        this.hasSizeChunk = hasSizeChunk;
    }

    public boolean hasMessage() {
        return hasMessage;
    }

    public void setHasMessage(boolean hasMessage) {
        this.hasMessage = hasMessage;
    }

    public boolean hasMessageChunk() {
        return hasMessageChunk;
    }

    public void setHasMessageChunk(boolean hasMessageChunk) {
        this.hasMessageChunk = hasMessageChunk;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        Flags flags = (Flags) o;
        return hasType==flags.hasType
                && hasSize==flags.hasSize
                && hasSizeChunk==flags.hasSizeChunk
                && hasMessage==flags.hasMessage
                && hasMessageChunk==flags.hasMessageChunk;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hasType, hasSize, hasSizeChunk, hasMessage, hasMessageChunk);
    }

    @Override
    public String toString() {
        return "Flags{" +
                "hasType=" + hasType +
                ", hasSize=" + hasSize +
                ", hasSizeChunk=" + hasSizeChunk +
                ", hasMessage=" + hasMessage +
                ", hasMessageChunk=" + hasMessageChunk +
                '}';
    }
}