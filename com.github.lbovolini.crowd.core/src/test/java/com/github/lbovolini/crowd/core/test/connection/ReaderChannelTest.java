package com.github.lbovolini.crowd.core.test.connection;

import com.github.lbovolini.crowd.core.connection.ReaderChannel;
import com.github.lbovolini.crowd.core.connection.ReaderChannelContext;
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
class ReaderChannelTest {

    @Mock
    private AsynchronousSocketChannel channel;

    @InjectMocks
    private ReaderChannelContext readerChannelContext;

    private ReaderChannel readerChannel;

    @BeforeEach
    void setUp() {
        readerChannel = new ReaderChannel(readerChannelContext);
    }

    @Test
    void shouldReturnTrueWhenQueueIsEmpty() {

        var shouldCallHandler = readerChannel.read();

        assertTrue(shouldCallHandler);
    }

    @Test
    void shouldReturnFalseWhenQueueIsNotEmpty() {
        readerChannelContext.getReaderBufferQueue().add(ByteBuffer.allocate(1));

        var shouldCallHandler = readerChannel.read();

        assertFalse(shouldCallHandler);
    }

    @Test
    void shouldCloseChannel() throws IOException {
        readerChannel.close();

        verify(channel, only()).close();
    }
}