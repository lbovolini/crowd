package com.github.lbovolini.crowd.discovery.worker;

import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.message.MulticastMessage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Set;

public abstract class MulticastWorker extends Thread implements MulticastService {

    protected final MulticastConnection connection;

    public MulticastWorker(MulticastConnection connection) {
        this.connection = connection;
    }

    /**
     * Event loop
     */
    @Override
    public void run() {
        try {
            onInit();

            var selector = connection.getSelector();

            while (true) {
                if (!selector.isOpen()) {
                    throw new RuntimeException("Selector is not open");
                }
                if (selector.select() == 0) { continue; }
                handle(selector.selectedKeys());
            }
        } catch (IOException e) { throw new UncheckedIOException(e); }
    }

    private void handle(final Set<SelectionKey> selectedKeys) throws IOException {

        var keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            var selectionKey = keyIterator.next();
            keyIterator.remove();

            if (!selectionKey.isValid()) { continue; }

            if (selectionKey.isReadable()) {
                connection.read((DatagramChannel) selectionKey.channel());
            } else if (selectionKey.isWritable()) {
                connection.write((DatagramChannel) selectionKey.channel(), (MulticastMessage) selectionKey.attachment());
            }
        }
    }

}
