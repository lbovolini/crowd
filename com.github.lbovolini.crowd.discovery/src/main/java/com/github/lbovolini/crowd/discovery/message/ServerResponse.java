package com.github.lbovolini.crowd.discovery.message;

import com.github.lbovolini.crowd.discovery.exception.MalformedMulticastServerResponseException;
import com.github.lbovolini.crowd.discovery.util.URLUtils;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class ServerResponse {

    public static final String SEPARATOR = ";";

    private final URL[] codebase;
    private final InetSocketAddress serverAddress;
    private final URL libURL;
    private final String type;

    public ServerResponse(URL[] codebase, InetSocketAddress serverAddress, URL libURL, String type) {
        this.codebase = Objects.requireNonNull(codebase);
        this.serverAddress = Objects.requireNonNull(serverAddress);
        this.libURL = Objects.requireNonNull(libURL);
        this.type = Objects.requireNonNull(type);
    }

    public static ServerResponse fromObject(Object response) {

        String[] info = response.toString().split(SEPARATOR);

        if (info.length < 5) {
            throw new MalformedMulticastServerResponseException("Malformed multicast server response");
        }

        URL[] codebase = URLUtils.split(info[0]);
        String address = info[1];
        int port = Integer.parseInt(info[2]);
        InetSocketAddress serverAddress = new InetSocketAddress(address, port);
        URL nativeLibURL = null;

        try {
            nativeLibURL = new URL(info[3]);
        } catch (MalformedURLException e) { e.printStackTrace(); }

        String type = info[4];

        return new ServerResponse(codebase, serverAddress, nativeLibURL, type);
    }

    public URL[] getCodebase() {
        return codebase;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public URL getLibURL() {
        return libURL;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return URLUtils.join(codebase)
                + SEPARATOR + serverAddress.getAddress().getHostAddress()
                + SEPARATOR + serverAddress.getPort()
                + SEPARATOR + libURL
                + SEPARATOR + type;
    }
}
