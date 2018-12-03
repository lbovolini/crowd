package com.github.lbovolini.crowd.common.configuration;

import com.github.lbovolini.crowd.common.host.Host;

public class Config {
    /**
     * TCP connection
     */
    public static final int BUFFER_SIZE = 1024;
    public static final int MAX_MESSAGE_SIZE = 1024;
    public static final int HEADER_SIZE = Byte.BYTES + Short.BYTES;
    public static final int BUFFER_ARRAY_SIZE = 16;

    public static final String HOST_NAME = System.getProperty("hostname", Host.getHostAddressName());
    public static final int PORT = Integer.parseInt(System.getProperty("port", String.valueOf(8081)));

     /**
     * MULTICAST connection
     */
    public static final String MULTICAST_IP = System.getProperty("multicast.ip", "225.4.5.6");
    public static final int MULTICAST_PORT = Integer.parseInt(System.getProperty("multicast.port", String.valueOf(8000)));

    public static final int MULTICAST_BUFFER_SIZE = 1024;
    public static final String MULTICAST_INTERFACE_NAME = System.getProperty("multicast.interface", Host.getNetworkInterfaceName());
    public static final int MULTICAST_CLIENT_PORT = Integer.parseInt(System.getProperty("multicast.client.port", String.valueOf(8011)));

    /**
     * MULTICAST messages
     */
    public static final Byte HEARTBEAT = (byte)-1;
    public static final Byte DISCOVER = (byte)1;
    //
    public static final char SEPARATOR = ';';

    /**
     * HEARTBEAT
     */
    public static final int HEARTBEAT_INTERVAL = 5;
    public static final int MAX_DOWNTIME = 6;

    /**
     * NON FINAL
     */
    public static String CODEBASE = System.getProperty("server.codebase", "");
    public static String LIBURL = System.getProperty("lib.url");
}
