package com.github.lbovolini.crowd.discovery.worker;

import com.github.lbovolini.crowd.discovery.connection.MulticastConnection;
import com.github.lbovolini.crowd.discovery.connection.MulticastIOChannelHandler;
import com.github.lbovolini.crowd.discovery.message.MulticastMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.Set;

public abstract class MulticastWorker extends Thread implements MulticastService {

    private static final Logger log = LoggerFactory.getLogger(MulticastWorker.class);

    protected final MulticastConnection connection;

    public MulticastWorker(MulticastConnection connection) {
        this.connection = connection;
    }

    /**
     * Event loop
     */
    @Override
    public void run() {

        log.info("Multicast worker starting");

        try {
            onInit();

            var selector = connection.getSelector();

            while (true) {
                if (selector.select() == 0) { continue; }
                handle(selector.selectedKeys());
            }
        } catch (ClosedSelectorException cse) {
            log.error("Selector is closed", cse);
        } catch (IOException ioe){
            log.error("Selector I/O error", ioe);
        }
    }

    private void handle(final Set<SelectionKey> selectedKeys) throws IOException {

        var keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            var selectionKey = keyIterator.next();
            keyIterator.remove();

            if (!selectionKey.isValid()) { continue; }

            if (selectionKey.isReadable()) {
                var message = MulticastIOChannelHandler.read((DatagramChannel) selectionKey.channel());

                if (message == null) {
                    continue;
                }

                connection.getContext().setServerAddress(message.getAddress());
                connection.getMessageHandler().handle(message);
                onReceive();
            } else if (selectionKey.isWritable()) {
                var shouldReceive = MulticastIOChannelHandler.write((DatagramChannel) selectionKey.channel(), (MulticastMessage) selectionKey.attachment());
                if (shouldReceive) {
                    connection.receive();
                }
            }
        }
    }

}
