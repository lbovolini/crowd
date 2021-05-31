package com.github.lbovolini.crowd.core.test.message;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageFactory;
import com.github.lbovolini.crowd.core.message.MessageType;
import com.github.lbovolini.crowd.core.message.messages.CreateObject;
import com.github.lbovolini.crowd.core.message.messages.InvokeMethod;
import com.github.lbovolini.crowd.core.message.messages.JoinGroup;
import com.github.lbovolini.crowd.core.message.messages.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MessageFactoryTest {

    @Test
    void shouldCreateCreateMessage() {
        // Input
        String className = MyObject.class.getName();
        Class<?>[] types = new Class[] { int.class };
        Object[] args =  new Object[] { 5 };

        // Expected output
        CreateObject createObject = new CreateObject(className, types, args);
        byte[] serializedMessageData = new byte[0];

        try {
            serializedMessageData = Message.serialize(createObject);
        } catch (IOException e) {
            fail(e);
        }

        // Should test ONLY this method
        Message message = MessageFactory.create(className, types, args);

        // Assertions
        assertNotNull(message);
        assertEquals(MessageType.CREATE.getType(), message.getType());
        assertArrayEquals(serializedMessageData, message.getData());
        assertEquals(serializedMessageData.length, message.getDataLength());
        assertEquals(serializedMessageData.length + Message.HEADER_SIZE, message.getSize());
        try {
            assertEquals(createObject, Message.deserialize(message.getData()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldCreateInvokeMessage() {
        // Input
        int requestId = 1;
        String method = null;

        try {
            method = MyObject.class.getMethod("say", null).getName();
        } catch (NoSuchMethodException e) {
            fail(e);
        }

        Class<?>[] types = null;
        Object[] args = null;

        // Expected output
        InvokeMethod invokeMethod = new InvokeMethod(requestId, method, types, args);
        byte[] serializedMessageData = new byte[0];

        try {
            serializedMessageData = Message.serialize(invokeMethod);
        } catch (IOException e) {
            fail(e);
        }

        // Should test ONLY this method
        Message message = MessageFactory.invoke(requestId, method, types, args);

        // Assertions
        assertNotNull(message);
        assertEquals(MessageType.INVOKE.getType(), message.getType());
        assertArrayEquals(serializedMessageData, message.getData());
        assertEquals(serializedMessageData.length, message.getDataLength());
        assertEquals(serializedMessageData.length + Message.HEADER_SIZE, message.getSize());
        try {
            assertEquals(invokeMethod, Message.deserialize(message.getData()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldCreateJoinMessage() {
        // Input
        int cores = 4;

        // Expected output
        JoinGroup joinGroup = new JoinGroup(cores);
        byte[] serializedMessageData = new byte[0];

        try {
            serializedMessageData = Message.serialize(joinGroup);
        } catch (IOException e) {
            fail(e);
        }

        // Should test ONLY this method
        Message message = MessageFactory.join(cores);

        // Assertions
        assertNotNull(message);
        assertEquals(MessageType.JOIN.getType(), message.getType());
        assertArrayEquals(serializedMessageData, message.getData());
        assertEquals(serializedMessageData.length, message.getDataLength());
        assertEquals(serializedMessageData.length + Message.HEADER_SIZE, message.getSize());
        try {
            assertEquals(joinGroup, Message.deserialize(message.getData()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldCreateReplayMessage() {
        // Input
        int requestId = 2;
        Object result = null;
        String exception = null;

        // Expected output
        Response response = new Response(requestId, result, exception);
        byte[] serializedMessageData = new byte[0];

        try {
            serializedMessageData = Message.serialize(response);
        } catch (IOException e) {
            fail(e);
        }

        // Should test ONLY this method
        Message message = MessageFactory.reply(requestId, result, exception);

        // Assertions
        assertNotNull(message);
        assertEquals(MessageType.REPLY.getType(), message.getType());
        assertArrayEquals(serializedMessageData, message.getData());
        assertEquals(serializedMessageData.length, message.getDataLength());
        assertEquals(serializedMessageData.length + Message.HEADER_SIZE, message.getSize());
        try {
            assertEquals(response, Message.deserialize(message.getData()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}