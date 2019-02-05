package com.github.lbovolini.crowd.message;

import com.github.lbovolini.crowd.message.messages.CreateObject;
import com.github.lbovolini.crowd.message.messages.InvokeMethod;
import com.github.lbovolini.crowd.message.messages.JoinGroup;
import com.github.lbovolini.crowd.message.messages.Response;

import java.io.IOException;

public class MessageFactory {

    public static Message create(String className, Class<?>[] types, Object[] args) throws IOException {
        CreateObject createObject = new CreateObject(className, types, args);
        return Message.create(Message.Type.CREATE, createObject);
    }

    public static Message invoke(int requestId, String method, Class<?>[] types, Object[] args) throws IOException {
        InvokeMethod invokeMethod = new InvokeMethod(requestId, method, types, args);
        return Message.create(Message.Type.INVOKE, invokeMethod);
    }

    public static Message join(int cores) throws IOException {
        JoinGroup joinGroup = new JoinGroup(cores);
        return Message.create(Message.Type.JOIN, joinGroup);
    }

    public static Message reply(int requestId, Object result, String exception) throws IOException {
        Response response = new Response(requestId, result, exception);
        return Message.create(Message.Type.REPLY, response);
    }
}
