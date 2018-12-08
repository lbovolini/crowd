package com.github.lbovolini.crowd.common.group;

public class ServerDetails {
    private final String codebase;
    private final String serverAddress;
    private final int serverPort;
    private final String libURL;
    private final boolean reconnect;

    public ServerDetails(String codebase, String serverAddress, int serverPort, String libURL, boolean reconnect) {
        this.codebase = codebase;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.libURL = libURL;
        this.reconnect = reconnect;
    }

    public ServerDetails(String codebase, String serverAddress, String serverPort, String libURL, String reconnect) {
        this(codebase, serverAddress, Integer.parseInt(serverPort), libURL, Boolean.valueOf(reconnect));
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

    public boolean isReconnect() {
        return reconnect;
    }
}
