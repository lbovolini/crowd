package com.github.lbovolini.crowd.core.message;

import com.github.lbovolini.crowd.core.message.messages.CreateObject;
import com.github.lbovolini.crowd.core.message.messages.InvokeMethod;
import com.github.lbovolini.crowd.core.message.messages.JoinGroup;
import com.github.lbovolini.crowd.core.message.messages.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;

import static com.github.lbovolini.crowd.core.message.MessageType.*;

public class MessageFactory {

    private static final Logger log = LoggerFactory.getLogger(MessageFactory.class);

    private MessageFactory() {}

    public static Message create(String className, Class<?>[] types, Object[] args) {
        CreateObject createObject = new CreateObject(className, types, args);

        try {
            return Message.create(CREATE, createObject);
        } catch (IOException e) {
            log.error("Error while creating message of type={}", CREATE);
            throw new UncheckedIOException(e);
        }
    }

    public static Message invoke(int requestId, String method, Class<?>[] types, Object[] args) {
        InvokeMethod invokeMethod = new InvokeMethod(requestId, method, types, args);

        try {
            return Message.create(INVOKE, invokeMethod);
        } catch (IOException e) {
            log.error("Error while creating message of type={}", INVOKE);
            throw new UncheckedIOException(e);
        }
    }

    public static Message join(int cores) {
        JoinGroup joinGroup = new JoinGroup(cores);

        try {
            return Message.create(JOIN, joinGroup);
        } catch (IOException e) {
            log.error("Error while creating message of type={}", JOIN);
            throw new UncheckedIOException(e);
        }
    }

    public static Message reply(int requestId, Object result, String exception) {
        Response response = new Response(requestId, result, exception);

        try {
            return Message.create(REPLY, response);
        } catch (IOException e) {
            log.error("Error while creating message of type={}", REPLY);
            throw new UncheckedIOException(e);
        }
    }
}
