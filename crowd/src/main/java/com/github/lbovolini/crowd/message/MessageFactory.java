package com.github.lbovolini.crowd.message;

import com.github.lbovolini.crowd.message.messages.CreateObject;
import com.github.lbovolini.crowd.message.messages.InvokeMethod;
import com.github.lbovolini.crowd.message.messages.JoinGroup;
import com.github.lbovolini.crowd.message.messages.Response;

import java.io.IOException;
import java.io.UncheckedIOException;

public class MessageFactory {

    public static Message create(String className, Class<?>[] types, Object[] args) throws IOException {
        CreateObject createObject = new CreateObject(className, types, args);
        return Message.create(Message.Type.CREATE, createObject);
    }

    public static Message invoke(int requestId, String method, Class<?>[] types, Object[] args) {
        InvokeMethod invokeMethod = new InvokeMethod(requestId, method, types, args);

        try {
            return Message.create(Message.Type.INVOKE, invokeMethod);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Message join(int cores) {
        JoinGroup joinGroup = new JoinGroup(cores);

        try {
            return Message.create(Message.Type.JOIN, joinGroup);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Message reply(int requestId, Object result, String exception) {
        Response response = new Response(requestId, result, exception);

        try {
            return Message.create(Message.Type.REPLY, response);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
