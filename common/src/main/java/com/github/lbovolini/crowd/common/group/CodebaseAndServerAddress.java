package com.github.lbovolini.crowd.common.group;

public class CodebaseAndServerAddress {
    private final String codebase;
    private final String serverAddress;
    private final int serverPort;

    public CodebaseAndServerAddress(String codebase, String serverAddress, int serverPort) {
        this.codebase = codebase;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public CodebaseAndServerAddress(String codebase, String serverAddress, String serverPort) {
        this.codebase = codebase;
        this.serverAddress = serverAddress;
        this.serverPort = Integer.parseInt(serverPort);
    }

    public String getCodebase() {
        return codebase;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }
}
