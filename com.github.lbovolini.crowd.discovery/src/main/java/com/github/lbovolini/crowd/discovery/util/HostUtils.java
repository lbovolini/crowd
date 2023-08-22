package com.github.lbovolini.crowd.discovery.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

public interface HostUtils {

    Logger log = LoggerFactory.getLogger(HostUtils.class);

    /**
     * Busca e retorna o primeiro endereço IP v4 da primeira interface de rede.
     * @return Endereço IP v4.
     * @throws SocketException
     */
    static String getHostAddress() throws SocketException {

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        if (nets == null) {
            throw new RuntimeException("No network interface found. Maybe you are using a virtual device");
        }

        List<NetworkInterface> netInterfaces = Collections.list(nets);
        netInterfaces.sort(Comparator.comparingInt(NetworkInterface::getIndex));

        for (NetworkInterface netint : netInterfaces) {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                    //System.out.println(inetAddress.getHostAddress());
                    return inetAddress.getHostAddress();
                }
            }
        }

        throw new RuntimeException("Host address not found");
    }

    static String getHostAddressName() {
        try {
            return getHostAddress();
        } catch (SocketException e) {
            log.error("Error while getting host address", e);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Busca e retorna o nome da primeira interface de rede.
     * @return O nome da interface de rede.
     * @throws SocketException
     */
    static String getNetworkInterface() throws SocketException {

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        if (nets == null) {
            throw new RuntimeException("No network interface found. Maybe you are using a virtual device");
        }

        List<NetworkInterface> netInterfaces = Collections.list(nets);
        netInterfaces.sort(Comparator.comparingInt(NetworkInterface::getIndex));

        for (NetworkInterface netint : netInterfaces) {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                    //System.out.println(inetAddress.getHostAddress());
                    return netint.getName();
                }
            }
        }

        throw new RuntimeException("Network interface not found");
    }

    static String getNetworkInterfaceName() {
        try {
            return getNetworkInterface();
        } catch (SocketException e) {
            log.error("Error while getting network interface name", e);
            throw new UncheckedIOException(e);
        }
    }
}
