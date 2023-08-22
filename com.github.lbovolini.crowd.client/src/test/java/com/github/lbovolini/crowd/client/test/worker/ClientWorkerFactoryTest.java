package com.github.lbovolini.crowd.client.test.worker;

import com.github.lbovolini.crowd.classloader.ClassLoaderContext;
import com.github.lbovolini.crowd.client.worker.ClientWorkerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientWorkerFactoryTest {

    @Test
    void shouldCreateDefaultWorker() {
        var context = new ClassLoaderContext("/tmp", "/tmp");

        var clientWorker = ClientWorkerFactory.defaultWorker(context);

        assertNotNull(clientWorker);
    }

}