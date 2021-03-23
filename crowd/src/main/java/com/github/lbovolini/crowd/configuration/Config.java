package com.github.lbovolini.crowd.configuration;

import com.github.lbovolini.crowd.utils.HostUtils;

/**
 * Contém todas as configurações da aplicação.
 */
public class Config {
    /**
     * TCP connection
     */
    public static final int BUFFER_SIZE = 1024;
    public static final int MAX_MESSAGE_SIZE = Short.MAX_VALUE;
    public static final int HEADER_SIZE = Byte.BYTES + Short.BYTES;
    public static final int BUFFER_ARRAY_SIZE = 16;

    public static final String HOST_NAME = System.getProperty("hostname", HostUtils.getHostAddressName());
    public static final int PORT = Integer.parseInt(System.getProperty("port", String.valueOf(8081)));

    public static final int POOL_SIZE = Integer.parseInt(System.getProperty("pool.size",  String.valueOf(Runtime.getRuntime().availableProcessors())));

     /**
     * MULTICAST connection
     */
    public static final String MULTICAST_IP = System.getProperty("multicast.ip", "225.4.5.6");
    public static final int MULTICAST_PORT = Integer.parseInt(System.getProperty("multicast.port", String.valueOf(8000)));

    // !important Should NOT be bigger than datagram packet max size
    public static final int MULTICAST_BUFFER_SIZE = 1024;
    public static final String MULTICAST_INTERFACE_NAME = System.getProperty("multicast.interface", HostUtils.getNetworkInterfaceName());
    public static final int MULTICAST_CLIENT_PORT = Integer.parseInt(System.getProperty("multicast.client.port", String.valueOf(8011)));

    //
    public static final String SEPARATOR = ";";

    /**
     * HEARTBEAT
     */
    public static final int HEARTBEAT_INTERVAL = 5;
    public static final int MAX_DOWNTIME = 15;

    public static final String LIB_URL = System.getProperty("lib.url", "file:");
    public static final String LIB_PATH = System.getProperty("lib.path", System.getProperty("java.io.tmpdir"));

    /**
     *
     */
    public static final String EXTENSIONS[] = {".class", ".jar", ".so", ".dll"};
    public static final String CODEBASE_ROOT = System.getProperty("codebase.root", "");
    public static final String CODEBASE_URL = System.getProperty("codebase.url", "file:");
    public static final String URL_SEPARATOR = System.getProperty("codebase.url", " ");

    public static final String CLASSLOADER = System.getProperty("classloader", "com.github.lbovolini.crowd.classloader.AndroidRemoteClassLoader");

    public static final int DEX_MIN_SDK_VERSION = Integer.parseInt(System.getProperty("dex.version", "26"));
    public static final boolean DEX_OPTIMIZE = Boolean.valueOf(System.getProperty("dex.optimize", "true"));

    public static final boolean CACHE = Boolean.valueOf(System.getProperty("cache", "false"));

    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final String VENDOR = System.getProperty("java.vendor");
    public static final String OS_NAME = System.getProperty("os.name");
}
