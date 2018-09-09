package com.github.lbovolini.crowd.common.configuration;

public class Config {
    public static final int BUFFER_SIZE = 1024;
    public static final int HEADER_SIZE = Byte.BYTES + Short.BYTES;
    public static final int BUFFER_ARRAY_SIZE = 16;

    public static final String SERVER_HOST = System.getProperty("server.hostname", "localhost");
    public static final int SERVER_PORT = Integer.parseInt(System.getProperty("server.port", String.valueOf(8080)));

    public static final String CLIENT_HOST = System.getProperty("client.hostname", "localhost");
    public static final int CLIENT_PORT = Integer.parseInt(System.getProperty("client.port", String.valueOf(8081)));

    public static final String CODEBASE = System.getProperty("server.codebase", "file:/home/lbovolini/Dev/quebra-pedra/qp-dist/target/classes/ file:/home/lbovolini/Dev/quebra-pedra/plugins/classes/ file:/home/lbovolini/Dev/quebra-pedra/qp/target/classes/");
}
