package com.github.lbovolini.crowd.discovery.connection;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;

public class MulticastReaderChannel {

    private final MulticastChannelContext context;

    public MulticastReaderChannel(MulticastChannelContext context) {
        this.context = context;
    }

    public void read() {
        try {
            context.getChannel().register(context.getSelector(), SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }
}
