package com.github.lbovolini.crowd.common.host;

import java.io.Serializable;

public class HostDetails implements Serializable {

    private final String id;
    private final String host;
    private final int port;
    private final int cores;

    public HostDetails(String id, String host, int port, int cores) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.cores = cores;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int availableProcessors() {
        return cores;
    }
}
