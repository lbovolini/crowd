package com.github.lbovolini.crowd.group;

import com.github.lbovolini.crowd.group.request.MulticastClientRequestHandler;
import com.github.lbovolini.crowd.group.request.MulticastRequestHandler;
import com.github.lbovolini.crowd.group.worker.MulticastClientWorker;
import com.github.lbovolini.crowd.group.worker.MulticastWorkerContext;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URL;

class MulticastClientWorkerTest {

    @Test
    void should() {
/*
        CodebaseService codebaseService = new CodebaseService() {
            @Override
            public void onConnect(URL[] codebase, URL libURL, InetSocketAddress serverAddress) {

            }

            @Override
            public void onUpdate(URL[] codebase, URL libURL) {

            }

            @Override
            public void onReload(URL[] codebase, URL libURL) {

            }
        };

        MulticastRequestHandler requestHandler = new MulticastClientRequestHandler(codebaseService);

        MulticastWorkerContext workerContext = new MulticastWorkerContext();
        MulticastClientWorker client = new MulticastClientWorker(requestHandler);
        client.setServerAddress(new InetSocketAddress(8888));

        client.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        client.send(HEARTBEAT);*/

    }

}