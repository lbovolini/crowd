package com.github.lbovolini.crowd.client.test.worker;

import com.github.lbovolini.crowd.client.worker.MulticastClientWorkerFactory;
import com.github.lbovolini.crowd.discovery.service.CodebaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MulticastClientWorkerFactoryTest {

    @Mock
    private CodebaseService codebaseService;

    @Test
    void shouldCreateDefaultClientWorker() {
        var clientWorker = MulticastClientWorkerFactory.defaultClientWorker(codebaseService);

        assertNotNull(clientWorker);
    }
}