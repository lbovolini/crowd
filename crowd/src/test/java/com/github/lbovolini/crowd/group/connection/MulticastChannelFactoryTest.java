package com.github.lbovolini.crowd.group.connection;

import com.github.lbovolini.crowd.utils.HostUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

import static com.github.lbovolini.crowd.configuration.Config.MULTICAST_IP;
import static org.junit.jupiter.api.Assertions.*;

class MulticastChannelFactoryTest {

    private Selector selector;
    private NetworkInterface networkInterface;
    private InetAddress groupAddress;
    private InetSocketAddress localAddress;

    {
        try {
            selector = Selector.open();
            networkInterface = NetworkInterface.getByName(HostUtils.getNetworkInterfaceName());
            groupAddress = InetAddress.getByName(MULTICAST_IP);
            localAddress = new InetSocketAddress(8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldCreateInitializedNonBlockingChannel() {
        // Should test ONLY this method
        DatagramChannel channel = MulticastChannelFactory.initializedChannel(selector, networkInterface, groupAddress, localAddress);

        // Assertions
        assertNotNull(channel);
        assertTrue(channel.isOpen());
        assertFalse(channel.isBlocking());
    }
}