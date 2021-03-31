package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.worker.WorkerContext;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class ReaderChannelHandler {

    private ReaderChannelHandler() {}

    public static void handle(WorkerContext context) {
        ByteBuffer[] byteBufferArray = context.getReaderChannelContext().getReaderBufferArray();
        context.getChannel().read(byteBufferArray,
                    0,
                    byteBufferArray.length,
                    0,
                    TimeUnit.SECONDS,
                    context,
                    ReaderChannelContext.getReaderChannelCompletionHandler());
    }
}
