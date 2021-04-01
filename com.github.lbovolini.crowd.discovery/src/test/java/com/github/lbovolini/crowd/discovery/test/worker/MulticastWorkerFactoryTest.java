package com.github.lbovolini.crowd.discovery.test.worker;

import com.github.lbovolini.crowd.discovery.service.CodebaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MulticastWorkerFactoryTest {

    @Mock
    private CodebaseService codebaseService;

 /*   @Test
    void shouldCreateDefaultClientWorker() {
        MulticastClientWorker clientWorker = MulticastWorkerFactory.defaultClientWorker(codebaseService);

        assertNotNull(clientWorker);
    }

    @Test
    void shouldCreateDefaultServerWorker() {
        MulticastServerWorker serverWorker = MulticastWorkerFactory.defaultServerWorker();

        assertNotNull(serverWorker);
    }*/
}