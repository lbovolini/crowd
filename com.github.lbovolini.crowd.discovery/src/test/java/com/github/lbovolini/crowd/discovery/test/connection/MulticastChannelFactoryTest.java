package com.github.lbovolini.crowd.discovery.test.connection;

import com.github.lbovolini.crowd.discovery.connection.MulticastChannelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

import static org.junit.jupiter.api.Assertions.*;

class MulticastChannelFactoryTest {

    private Selector selector;
    private NetworkInterface networkInterface;
    private InetAddress groupAddress;
    private InetSocketAddress localAddress;

    {
        try {
            selector = Selector.open();
            // !TODO
            networkInterface = NetworkInterface.getByIndex(1);
            groupAddress = InetAddress.getByName("224.5.4.3");
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