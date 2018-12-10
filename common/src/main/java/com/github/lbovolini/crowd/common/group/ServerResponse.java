package com.github.lbovolini.crowd.common.group;

import static com.github.lbovolini.crowd.common.configuration.Config.SEPARATOR;

public class ServerResponse {
    private final String codebase;
    private final String serverAddress;
    private final int serverPort;
    private final String libURL;
    private final String type;

    public ServerResponse(String codebase, String serverAddress, int serverPort, String libURL, String type) {
        this.codebase = codebase;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.libURL = libURL;
        this.type = type;
    }

    public ServerResponse(String codebase, String serverAddress, String serverPort, String libURL, String type) {
        this(codebase, serverAddress, Integer.parseInt(serverPort), libURL, type);
    }

    public static ServerResponse fromObject(Object response) {
        String[] info = response.toString().split(SEPARATOR);

        if (info.length < 5) {
            throw new RuntimeException("Server response error");
        }
        return new ServerResponse(info[0], info[1], info[2], info[3], info[4]);
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

    public String getType() {
        return type;
    }
}
