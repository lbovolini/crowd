package com.github.lbovolini.crowd.common.host;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public interface Host {
    static String getHostAddress() throws SocketException {

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        for (NetworkInterface netint : Collections.list(nets)) {
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

        String address = "";

        try {
            address = getHostAddress();
        } catch (SocketException e) { }
        return address;
    }

    static String getNetworkInterface() throws SocketException {

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        for (NetworkInterface netint : Collections.list(nets)) {
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
        String netInt = "";

        try {
            netInt = getNetworkInterface();
        } catch (SocketException e) {  }
        return netInt;
    }
}
