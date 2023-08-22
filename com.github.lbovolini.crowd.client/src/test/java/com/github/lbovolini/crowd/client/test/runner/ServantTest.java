package com.github.lbovolini.crowd.client.test.runner;

import com.github.lbovolini.crowd.client.runner.Servant;
import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.messages.InvokeMethod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServantTest {

    @Mock
    private Connection connection;

    @Test
    void shouldExecuteMethod() {
        var invokeMethod = new InvokeMethod(1, "toString", null, null);

        Servant.execute(new Object(), invokeMethod, connection);

        verify(connection).send(any(Message.class));
    }

    @Test
    void shouldCatchException() {
        var invokeMethod = new InvokeMethod(1, "hello", null, null);

        Servant.execute(new Object(), invokeMethod, connection);

        verify(connection).send(any(Message.class));
    }
}