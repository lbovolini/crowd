package com.github.lbovolini.crowd.client.codebase;

import com.github.lbovolini.crowd.client.worker.ClientWorker;
import com.github.lbovolini.crowd.classloader.ClassLoaderContext;
import com.github.lbovolini.crowd.discovery.service.CodebaseService;

import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.channels.ClosedChannelException;

public class CodebaseServiceImpl implements CodebaseService {

    private final ClassLoaderContext context;
    private final ClientWorker worker;

    public CodebaseServiceImpl(ClassLoaderContext context, ClientWorker worker) {
        this.context = context;
        this.worker = worker;
    }

    @Override
    public void onConnect(URL[] codebase, URL libURL, InetSocketAddress serverAddress) {
        context.setClassURLs(codebase);
        context.setLibURL(libURL);

        if (worker.isConnected()) {
            worker.reconnect(serverAddress);
        }
        else {
            worker.connect(serverAddress);
        }
    }

    @Override
    public void onUpdate(URL[] codebase, URL libURL) {
        context.setClassURLs(codebase);
        context.setLibURL(libURL);
    }

    @Override
    public void onReload(URL[] codebase, URL libURL) {
        context.setClassURLs(codebase);
        context.setLibURL(libURL);
    }
}
