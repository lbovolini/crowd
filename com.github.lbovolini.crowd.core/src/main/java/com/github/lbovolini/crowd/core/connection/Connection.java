package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.request.RequestQueue;
import com.github.lbovolini.crowd.core.worker.WorkerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;

public class Connection {

    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    private final AsynchronousSocketChannel channel;
    private final ReaderChannel readerChannel;
    private final WriterChannel writerChannel;

    private final WorkerContext workerContext;

    public Connection(AsynchronousSocketChannel channel, ReaderChannel readerChannel, WriterChannel writerChannel, RequestQueue requestQueue) {
        this.channel = channel;
        this.readerChannel = readerChannel;
        this.writerChannel = writerChannel;
        this.workerContext = new WorkerContext(readerChannel.getContext(), writerChannel.getContext(), new MessageHandler(this, requestQueue));
    }

    public long getHostId() {
        try {
            InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
            String host = Objects.requireNonNull(address.getAddress().getHostAddress().replace(".", ""));
            String port = Objects.requireNonNull(Integer.toString(address.getPort()));

            if (host.trim().isEmpty() || port.trim().isEmpty() || port.trim().equals("0")) {
                throw new RuntimeException("Invalid remote address");
            }
            return Long.parseLong(host + port);
        } catch (IOException e) {
            log.error("Error while getting host id");
            throw new UncheckedIOException(e);
        }
    }

    public String getRemoteAddress() {
        try {
            return ((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress();
        } catch (IOException e) {
            log.error("Error while getting remote host address");
            throw new UncheckedIOException(e);
        }
    }

    public int getRemotePort() {
        try {
            return ((InetSocketAddress) channel.getRemoteAddress()).getPort();
        } catch (IOException e) {
            log.error("Error while getting remote host port");
            throw new UncheckedIOException(e);
        }
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public void send(Message message) {
        if (writerChannel.write(message.getType(), message.getData())) {
            WriterChannelHandler.handle(workerContext);
        }
    }

    public void receive() {
        if (readerChannel.read()) {
            ReaderChannelHandler.handle(workerContext);
        }
    }

    public void close() throws IOException {
        readerChannel.close();
        writerChannel.close();
    }
}
