package com.github.lbovolini.crowd.discovery.test.connection;

import com.github.lbovolini.crowd.discovery.connection.MulticastChannelContext;
import com.github.lbovolini.crowd.discovery.connection.MulticastChannelFactory;
import com.github.lbovolini.crowd.discovery.connection.MulticastWriterChannel;
import com.github.lbovolini.crowd.discovery.message.ClientResponseFactory;
import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.message.ResponseFactory;
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

import static com.github.lbovolini.crowd.discovery.message.MulticastMessageType.HEARTBEAT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MulticastWriterChannelTest {

    private Selector selector;
    private NetworkInterface networkInterface;
    private InetAddress groupAddress;
    private static final int MULTICAST_SERVER_PORT = 8001;
    private InetSocketAddress localAddress;
    private InetSocketAddress remoteAddress;
    private InetSocketAddress serverGroupAddress;

    {
        try {
            selector = Mockito.spy(Selector.open());
            // !TODO
            networkInterface = NetworkInterface.getByIndex(1);
            groupAddress = InetAddress.getByName("224.5.4.3");
            localAddress = new InetSocketAddress(8888);
            remoteAddress = new InetSocketAddress(8008);
            serverGroupAddress = new InetSocketAddress(groupAddress.getHostName(), MULTICAST_SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DatagramChannel datagramChannel;
    private MulticastWriterChannel writerChannel;

    @BeforeEach
    void setUp() {
        datagramChannel = Mockito.spy(MulticastChannelFactory.initializedChannel(selector, networkInterface, groupAddress, localAddress));
        ResponseFactory responseFactory = new ClientResponseFactory();
        MulticastChannelContext context = new MulticastChannelContext(datagramChannel, selector, networkInterface, groupAddress, localAddress, responseFactory, MULTICAST_SERVER_PORT, false);
        writerChannel = new MulticastWriterChannel(context);
    }

    @Test
    void shouldRegisterWriteOperationInSelector() {
        // Input
        byte message = HEARTBEAT.getType();
        byte[] data = new byte[] { MulticastMessageType.HEARTBEAT.getType() };
        MulticastMessage multicastMessage = new MulticastMessage(data, data.length, remoteAddress);

        try {
            // Should test ONLY this method
            writerChannel.write(message, remoteAddress);

            // Assertions
            verify(datagramChannel, times(1)).register(selector, SelectionKey.OP_WRITE, multicastMessage);
        } catch (ClosedChannelException e) {
            fail(e);
        }
    }

    @Test
    void shouldWakeUpSelectorAfterWriteOperationBeRegisteredInSelector() {
        // Input
        byte message = HEARTBEAT.getType();

        // Should test ONLY this method
        writerChannel.write(message, remoteAddress);

        // Assertions
        verify(selector, times(1)).wakeup();
    }

    @Test
    void shouldThrowUncheckedIOExceptionWhenChannelIsClosed() {
        // Input
        byte message = HEARTBEAT.getType();

        try {
            datagramChannel.close();

            // Should test ONLY this method
            assertThrows(UncheckedIOException.class, () -> {
                writerChannel.write(message, remoteAddress);
            });

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRemoteHostAddressIsNull() {
        // Expected exception message
        String expectedExceptionMessage = "Receiver host address cannot be null";

        // Input
        byte message = HEARTBEAT.getType();

        try {
            datagramChannel.close();

            // Should test ONLY this method
            Throwable throwable = assertThrows(IllegalArgumentException.class, () -> {
                writerChannel.write(message, null);
            });

            assertEquals(expectedExceptionMessage, throwable.getMessage());

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenRemoteHostAddressIsNotSet() {
        // Expected exception message
        String expectedExceptionMessage = "Receiver host address cannot be null";

        // Input
        byte message = HEARTBEAT.getType();

        try {
            datagramChannel.close();

            // Should test ONLY this method
            Throwable throwable = assertThrows(IllegalArgumentException.class, () -> {
                writerChannel.write(message);
            });

            assertEquals(expectedExceptionMessage, throwable.getMessage());

        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void shouldRegisterWriteOperationInSelectorWhenWriteGroupMethodIsCalled() {
        // Input
        byte message = HEARTBEAT.getType();
        byte[] data = new byte[] { MulticastMessageType.HEARTBEAT.getType() };
        MulticastMessage multicastMessage = new MulticastMessage(data, data.length, serverGroupAddress);

        try {
            // Should test ONLY this method
            writerChannel.writeGroup(message);

            // Assertions
            verify(datagramChannel, times(1)).register(selector, SelectionKey.OP_WRITE, multicastMessage);
        } catch (ClosedChannelException e) {
            fail(e);
        }
    }
}