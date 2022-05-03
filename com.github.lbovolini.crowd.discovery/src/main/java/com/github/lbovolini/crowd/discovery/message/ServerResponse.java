package com.github.lbovolini.crowd.discovery.message;

import com.github.lbovolini.crowd.discovery.exception.InvalidMulticastMessageException;
import com.github.lbovolini.crowd.discovery.exception.MalformedMulticastServerResponseException;
import com.github.lbovolini.crowd.discovery.util.URLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class ServerResponse {

    private static final Logger log = LoggerFactory.getLogger(ServerResponse.class);

    public static final String SEPARATOR = ";";
    public static final int PARAMETERS = 5;

    private final URL[] codebase;
    private final InetSocketAddress serverAddress;
    private final URL libURL;
    private final byte type;

    public ServerResponse(URL[] codebase, InetSocketAddress serverAddress, URL libURL, byte type) {
        this.codebase = Objects.requireNonNull(codebase);
        this.serverAddress = Objects.requireNonNull(serverAddress);
        this.libURL = Objects.requireNonNull(libURL);
        this.type = type;
    }

    public static ServerResponse fromObject(String response) {

        String[] info = splitResponse(response);

        try {
            URL[] codebase = URLUtils.split(info[0]);
            InetSocketAddress serverAddress = getServerAddress(info[1], info[2]);
            URL nativeLibURL = getNativeLibURL(info[3]);
            byte type = getType(info[4]);

            return new ServerResponse(codebase, serverAddress, nativeLibURL, type);
        } catch (MalformedURLException | NumberFormatException e) {
            log.error("Invalid server response", e);
            throw new MalformedMulticastServerResponseException(e);
        }
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

    public byte getType() {
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

    private static String[] splitResponse(String response) {

        if (response == null) {
            throw new MalformedMulticastServerResponseException("Malformed multicast server response. Response message is null");
        }

        String[] info = response.split(SEPARATOR);

        if (info.length != PARAMETERS) {
            throw new MalformedMulticastServerResponseException(String.format("Malformed multicast server response. Response message must have exactly %d parameters", PARAMETERS));
        }

        for (String data : info) {
            if (data.trim().isEmpty()) {
                throw new MalformedMulticastServerResponseException("Malformed multicast server response. Some parameter is invalid");
            }
        }

        return info;
    }

    private static byte getType(String stringType) {

        byte type = Byte.parseByte(stringType);

        MulticastMessageType messageType = MulticastMessageType.get(type);

        if (messageType == null) {
            throw new InvalidMulticastMessageException(String.format("Unknown multicast message type of type: %s ", stringType));
        }

        return type;
    }

    private static InetSocketAddress getServerAddress(String address, String port) {
        try {
            int portNumber = Integer.parseInt(port);

            Objects.requireNonNull(portNumber == 0 ? null : portNumber);

            return new InetSocketAddress(address, portNumber);
        } catch (RuntimeException e) {
            throw new InvalidMulticastMessageException("Invalid server address or port number", e);
        }
    }

    private static URL getNativeLibURL(String nativeLibURLString) throws MalformedURLException {
        return new URL(nativeLibURLString);
    }
}
