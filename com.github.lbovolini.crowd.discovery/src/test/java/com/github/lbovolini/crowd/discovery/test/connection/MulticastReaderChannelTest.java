package com.github.lbovolini.crowd.discovery.test.connection;

import com.github.lbovolini.crowd.discovery.connection.MulticastChannelContext;
import com.github.lbovolini.crowd.discovery.connection.MulticastChannelFactory;
import com.github.lbovolini.crowd.discovery.connection.MulticastReaderChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MulticastReaderChannelTest {

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

    private DatagramChannel datagramChannel;
    private MulticastReaderChannel readerChannel;

    @BeforeEach
    void setUp() {
        datagramChannel = Mockito.spy(MulticastChannelFactory.initializedChannel(selector, networkInterface, groupAddress, localAddress));
        MulticastChannelContext context = new MulticastChannelContext(datagramChannel, selector, networkInterface, groupAddress, localAddress, null, 0, false);
        readerChannel = new MulticastReaderChannel(context);
    }

    @Test
    void shouldRegisterReadOperationInSelector() {
        try {
            // Should test ONLY this method
            readerChannel.read();

            // Assertions
            verify(datagramChannel, times(1)).register(selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            fail(e);
        }
    }

    @Test
    void shouldThrowUncheckedIOExceptionWhenChannelIsClosed() {
        try {
            datagramChannel.close();

            // Should test ONLY this method
            assertThrows(UncheckedIOException.class, () -> {
                readerChannel.read();
            });

        } catch (IOException e) {
            fail(e);
        }
    }
}