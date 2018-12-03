package com.github.lbovolini.crowd.common.group;

public class CodebaseAndServerAddress {
    private final String codebase;
    private final String serverAddress;
    private final int serverPort;
    private final String libURL;

    public CodebaseAndServerAddress(String codebase, String serverAddress, int serverPort, String libURL) {
        this.codebase = codebase;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.libURL = libURL;
    }

    public CodebaseAndServerAddress(String codebase, String serverAddress, String serverPort, String libURL) {
        this.codebase = codebase;
        this.serverAddress = serverAddress;
        this.serverPort = Integer.parseInt(serverPort);
        this.libURL = libURL;
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

    public String getLibURL() {
        return libURL;
    }
}
