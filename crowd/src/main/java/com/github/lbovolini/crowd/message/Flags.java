package com.github.lbovolini.crowd.message;

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
}