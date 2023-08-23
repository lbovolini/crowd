package com.github.lbovolini.crowd.client.request;

import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.messages.CreateObject;
import com.github.lbovolini.crowd.core.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class HandleCreateRequest {

    private static final Logger log = LoggerFactory.getLogger(HandleCreateRequest.class);

    public static Object handle(Request request) {
        try {
            CreateObject createObject = (CreateObject) Message.deserialize(request.getMessage().getData());

            Class<?> classDefinition = Thread.currentThread().getContextClassLoader().loadClass(createObject.getName());
            Constructor<?> constructor = classDefinition.getConstructor(createObject.getParameterTypes());

            return constructor.newInstance(createObject.getArgs());
        } catch (Exception e) {
            log.debug("Error while creating object", e);
            return null;
        }
    }
}
