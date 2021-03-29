package com.github.lbovolini.crowd.core.connection;

import com.github.lbovolini.crowd.core.worker.WorkerContext;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class WriterChannelHandler {
    
    private WriterChannelHandler() {}

    public static void handle(WorkerContext context) {
        ByteBuffer[] byteBufferArray = context.getWriterChannelContext().getWriterBufferArray();
        context.getChannel().write(byteBufferArray, 
                0, 
                byteBufferArray.length, 
                0, 
                TimeUnit.SECONDS, 
                context, 
                WriterChannelContext.getWriterChannelCompletionHandler());

    }
}
