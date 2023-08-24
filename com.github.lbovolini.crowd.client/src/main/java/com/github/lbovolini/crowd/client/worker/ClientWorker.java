package com.github.lbovolini.crowd.client.worker;

import com.github.lbovolini.crowd.client.request.ClientRequestHandler;
import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelContext;
import com.github.lbovolini.crowd.core.connection.ClientConnectionChannelCompletionHandler;
import com.github.lbovolini.crowd.core.request.RequestHandler;
import com.github.lbovolini.crowd.core.request.Scheduler;
import com.github.lbovolini.crowd.core.util.HostUtils;
import com.github.lbovolini.crowd.core.worker.ChannelFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class ClientWorker {

    private AsynchronousSocketChannel channel;
    private ClientConnectionChannelContext clientInfo;
    private boolean isConnected = false;

    public ClientWorker(AsynchronousSocketChannel channel, ClientConnectionChannelContext clientInfo) {
        this.channel = channel;
        this.clientInfo = clientInfo;
    }

    public void connect(InetSocketAddress serverAddress) {
        ClientConnectionChannelCompletionHandler connectionChannelHandler = new ClientConnectionChannelCompletionHandler();
        channel.connect(serverAddress, clientInfo, connectionChannelHandler);
        isConnected = true;
    }

    public void reconnect(InetSocketAddress serverAddress) {
        close();

        InetSocketAddress localAddress = new InetSocketAddress(HostUtils.getHostAddressName(), ClientWorkerFactory.PORT);
        AsynchronousSocketChannel channel = ChannelFactory.initializedChannel(localAddress);
        ClientConnectionChannelContext clientInfo = new ClientConnectionChannelContext(channel, this.clientInfo.getScheduler(), this.clientInfo.getCores());

        this.channel = channel;
        this.clientInfo = clientInfo;

        connect(serverAddress);
    }

    public void close() {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
