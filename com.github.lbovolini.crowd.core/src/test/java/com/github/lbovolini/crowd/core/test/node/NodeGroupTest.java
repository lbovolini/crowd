package com.github.lbovolini.crowd.core.test.node;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.node.NodeGroup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeGroupTest {

    static class TestObject implements Serializable {

    }

    private final NodeGroup<?> nodeGroup = new NodeGroup<>(TestObject.class.getName());

    @Mock
    private Connection connection;

    @Test
    void shouldAddNodeToGroup() {
        // Input
        int cores = 4;

        nodeGroup.setOperation(o -> System.out.println("Hello"));

        // Should test ONLY this method
        nodeGroup.join(cores, connection);

        verify(connection, times(1)).getHostId();
    }
}