package com.github.lbovolini.crowd.group.worker;

import com.github.lbovolini.crowd.group.connection.MulticastReaderChannelHandler;
import com.github.lbovolini.crowd.group.connection.MulticastWriterChannelHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public abstract class MulticastWorker extends Thread implements MulticastService {

    protected final MulticastWorkerContext context;

    public MulticastWorker(MulticastWorkerContext context) {
        this.context = context;
    }

    /**
     * Event loop
     */
    @Override
    public void run() {
        try {
            onInit();

            Selector selector = context.getChannelContext().getSelector();

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

        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey selectionKey = keyIterator.next();
            keyIterator.remove();

            if (!selectionKey.isValid()) { continue; }

            if (selectionKey.isReadable()) {
                MulticastReaderChannelHandler.handle(selectionKey, context);
            } else if (selectionKey.isWritable()) {
                MulticastWriterChannelHandler.handle(selectionKey, context);
            }
        }
    }

}
