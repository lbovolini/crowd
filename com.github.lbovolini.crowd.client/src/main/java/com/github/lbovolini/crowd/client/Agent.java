package com.github.lbovolini.crowd.client;

import com.github.lbovolini.crowd.client.codebase.CodebaseServiceImpl;
import com.github.lbovolini.crowd.client.worker.ClientWorker;
import com.github.lbovolini.crowd.client.worker.ClientWorkerFactory;
import com.github.lbovolini.crowd.classloader.ClassLoaderContext;
import com.github.lbovolini.crowd.discovery.service.CodebaseService;
import com.github.lbovolini.crowd.client.worker.MulticastClientWorker;
import com.github.lbovolini.crowd.client.worker.MulticastClientWorkerFactory;

public final class Agent {

    public static final String CLASS_PATH = System.getProperty("class.path", null);
    public static final String LIB_PATH = System.getProperty("lib.path", System.getProperty("java.io.tmpdir"));

    public void start()  {

        ClassLoaderContext context = new ClassLoaderContext(CLASS_PATH, LIB_PATH);

        ClientWorker worker = ClientWorkerFactory.defaultWorker(context);

        CodebaseService codebaseService = new CodebaseServiceImpl(context, worker);

        MulticastClientWorker multicastWorker = MulticastClientWorkerFactory.defaultClientWorker(codebaseService);
        multicastWorker.start();
    }

    public static void main(String[] args) {
        new Agent().start();
    }
}