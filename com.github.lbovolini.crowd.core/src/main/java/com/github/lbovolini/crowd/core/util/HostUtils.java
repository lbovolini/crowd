package com.github.lbovolini.crowd.core.util;

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
        return "";
    }

    static String getHostAddressName() {
        try {
            return getHostAddress();
        } catch (SocketException e) {
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

        return "";
    }

    static String getNetworkInterfaceName() {
        try {
            return getNetworkInterface();
        } catch (SocketException e) {
            throw new UncheckedIOException(e);
        }
    }
}
