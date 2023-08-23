package com.github.lbovolini.crowd.discovery.test.connection;

import com.github.lbovolini.crowd.discovery.connection.MulticastChannelContext;
import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import com.github.lbovolini.crowd.discovery.message.MulticastMessageType;
import com.github.lbovolini.crowd.discovery.request.MulticastDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import static com.github.lbovolini.crowd.discovery.message.MulticastMessageType.HEARTBEAT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MulticastConnectionTest {

    @Mock
    private DatagramChannel channel;
    @Mock
    private Selector selector;
    @Mock
    private NetworkInterface networkInterface;
    @Mock
    private MulticastDispatcher dispatcher;

    private MulticastConnection connection;

    @BeforeEach
    void setUp() throws UnknownHostException {
        MulticastChannelContext context = new MulticastChannelContext(channel, selector, networkInterface, InetAddress.getByName("225.4.5.6"), new InetSocketAddress(8909), (type) -> new byte[]{}, 8000, false);
        context.setServerAddress(new InetSocketAddress(8888));
        connection = new MulticastConnection(context, dispatcher);
    }

    @Test
    void shouldSendMessageToServer() throws ClosedChannelException {
        // Input
        MulticastMessageType messageType = HEARTBEAT;

        // Should test ONLY this method
        connection.send(messageType);

        // Assertions
        verify(channel, only()).register(any(Selector.class), eq(SelectionKey.OP_WRITE), any(MulticastMessage.class));
    }

    @Test
    void shouldSendMessageToAllHosts() throws ClosedChannelException {
        // Input
        MulticastMessageType messageType = HEARTBEAT;

        // Should test ONLY this method
        connection.multicastSend(messageType);

        // Assertions
        verify(channel, only()).register(any(Selector.class), eq(SelectionKey.OP_WRITE), any(MulticastMessage.class));
    }

    @Test
    void shouldSendMessageToSpecificHost() throws ClosedChannelException {
        // Input
        MulticastMessageType messageType = HEARTBEAT;
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 0);

        // Should test ONLY this method
        connection.send(messageType, address);

        // Assertions
        verify(channel, only()).register(any(Selector.class), eq(SelectionKey.OP_WRITE), any(MulticastMessage.class));
    }

    @Test
    void shouldReceiveMessage() throws ClosedChannelException {
        // Should test ONLY this method
        connection.receive();

        // Assertions
        verify(channel, only()).register(any(Selector.class), eq(SelectionKey.OP_READ));
    }
}