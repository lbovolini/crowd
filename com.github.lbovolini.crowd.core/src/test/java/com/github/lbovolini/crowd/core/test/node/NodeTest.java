package com.github.lbovolini.crowd.core.test.node;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.node.Node;
import com.github.lbovolini.crowd.core.object.RemoteObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeTest {

    @Mock
    private Connection connection;

    @Mock
    private RemoteObject remoteObject;

    private final Integer cores = 4;

    private Node node;

    @BeforeEach
    void setUp() {
        node = new Node(cores, connection);
    }

    @Test
    void shouldReturnNodeId() {
        // Expected output
        long expectedOutput = 1270018088L;

        // Mock behaviors
        when(connection.getHostId()).thenReturn(1270018088L);

        // Should test ONLY this method
        long nodeId = node.getId();

        // Assertions
        assertEquals(expectedOutput, nodeId);
    }

    @Test
    void shouldReturnNumberOfCores() {
        // Expected output
        int expectedOutput = cores;

        // Should test ONLY this method
        int cores = node.cores();

        // Assertions
        assertEquals(expectedOutput, cores);
    }


    @Test
    void shouldReturnConnection() {
        // Should test ONLY this method
        Connection nodeConnection = node.getConnection();

        // Assertions
        assertEquals(connection, nodeConnection);
        assertNotNull(nodeConnection);
    }

    @Test
    void shouldReturnNullRemoteObject() {
        // Should test ONLY this method
        RemoteObject remoteObject = node.getRemoteObject();

        // Assertions
        assertNull(remoteObject);
    }

    @Test
    void shouldSetRemoteObject() {
        // Should test ONLY this method
        node.setRemoteObject(remoteObject);

        // Assertions
        assertEquals(remoteObject, node.getRemoteObject());
    }
}