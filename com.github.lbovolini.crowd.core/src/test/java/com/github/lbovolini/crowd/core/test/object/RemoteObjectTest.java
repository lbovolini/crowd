package com.github.lbovolini.crowd.core.test.object;

import com.github.lbovolini.crowd.core.node.Node;
import com.github.lbovolini.crowd.core.object.RemoteObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoteObjectTest {

    @Mock
    private Node node;

    @Test
    void shouldCreateNewInstanceWithUnParametrizedClassConstructor() throws Exception {
        // Should test ONLY this method
        Greetings greetings = (Greetings) RemoteObject.newInstance(HelloWorld.class.getName(), node);

        // Assertions
        assertNotNull(greetings);
    }

    @Test
    void shouldCreateNewInstanceWithParametrizedClassConstructor() throws Exception {
        // Should test ONLY this method
        Greetings greetings = (Greetings) RemoteObject.newInstance(HelloWorld.class.getName(), new Object[] { "Hi World" }, new Class[] { String.class }, node);

        // Assertions
        assertNotNull(greetings);
    }

    // !TODO try fix unchecked cast. See super type token
    @Test
    void shouldSendInvokeMessageWithObjectAsReturnTypeOfMethod() throws Exception {
        // Input
        Greetings<CompletableFuture<Void>> greetings = (Greetings<CompletableFuture<Void>>) RemoteObject.newInstance(HelloWorld.class.getName(), node);

        // Should test ONLY this method
        CompletableFuture<Void> hi = greetings.hi();

        // Assertions
        verify(node, times(2)).send(any());
        assertNotNull(hi);
    }

    // !TODO lazy remote object creation ?
    @Test
    void shouldSendInvokeMessageWithPrimitiveVoidAsReturnTypeOfMethod() throws Exception {
        // Input
        Greetings<CompletableFuture<Void>> greetings = (Greetings<CompletableFuture<Void>>) RemoteObject.newInstance(HelloWorld.class.getName(), node);

        // Should test ONLY this method
        greetings.hello();

        // Assertions
        verify(node, times(2)).send(any());
    }

    void shouldReturnPromise() throws Exception { }
}
