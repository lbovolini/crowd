package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.connection.WriterChannel;
import com.github.lbovolini.crowd.core.connection.WriterChannelContext;
import com.github.lbovolini.crowd.core.message.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WriterChannelTest {

    @Mock
    private AsynchronousSocketChannel channel;
    @InjectMocks
    private WriterChannelContext writerChannelContext;

    private WriterChannel writerChannel;

    @BeforeEach
    void setUp() {
        writerChannel = new WriterChannel(writerChannelContext);
    }

    @Test
    void shouldReturnTrueWhenQueueIsEmpty() {
        byte type = MessageType.HEARTBEAT.getType();
        byte[] data = new byte[]{1};

        var shouldCallHandler = writerChannel.write(type, data);

        assertTrue(shouldCallHandler);
    }

    @Test
    void shouldReturnFalseWhenQueueIsNotEmpty() {
        byte type = MessageType.HEARTBEAT.getType();
        byte[] data = new byte[]{1};

        writerChannelContext.getWriterBufferQueue().add(ByteBuffer.allocate(1));

        var shouldCallHandler = writerChannel.write(type, data);

        assertFalse(shouldCallHandler);
    }

    @Test
    void shouldCloseChannel() throws IOException {
        writerChannel.close();

        verify(channel, only()).close();
    }
}