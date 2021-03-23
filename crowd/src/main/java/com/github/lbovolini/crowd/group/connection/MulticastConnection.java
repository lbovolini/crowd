package com.github.lbovolini.crowd.group.connection;

import com.github.lbovolini.crowd.group.message.MulticastMessageType;

import java.net.InetSocketAddress;

public class MulticastConnection {

    private final MulticastReaderChannel readerChannel;
    private final MulticastWriterChannel writerChannel;

    public MulticastConnection(MulticastReaderChannel readerChannel, MulticastWriterChannel writerChannel) {
        this.readerChannel = readerChannel;
        this.writerChannel = writerChannel;
    }

    public void send(MulticastMessageType type) {
        writerChannel.write(type.getType());
    }

    public void send(MulticastMessageType type, InetSocketAddress address) {
        writerChannel.write(type.getType(), address);
    }

    public void multicastSend(MulticastMessageType type) {
        writerChannel.writeGroup(type.getType());
    }

    public void receive() {
        readerChannel.read();
    }

}
