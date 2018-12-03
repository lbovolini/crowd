package com.github.lbovolini.crowd.client;

import com.github.lbovolini.crowd.common.message.Message;
import com.github.lbovolini.crowd.common.message.request.InvokeMethod;
import com.github.lbovolini.crowd.common.message.request.Request;
import com.github.lbovolini.crowd.common.message.response.Response;
import com.github.lbovolini.crowd.common.connection.Connection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Servant {

    public static void execute(final Object object, Request request, Connection connection) {
        Object result = null;
        Exception exception = null;

        try {
            Object[] args = request.getArgs();
            Class[] types = getTypes(args);
            result = object.getClass().getMethod(request.getName(),
                    types).invoke(object, request.getArgs());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            exception = ex;
            ex.printStackTrace();
        }

        if (request instanceof InvokeMethod) {
            reply(new Response(Client.getId(), ((InvokeMethod) request).getUuid(), result, exception), connection);
        }
    }

    public static void reply(Response response, Connection connection) {
        try {
            byte[] data = Message.serialize(response);
            Message message = Message.create((byte)3, data);

            connection.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Class[] getTypes(Object[] args) {
        Class[] types = new Class[args.length];

        for (int i = 0; i < types.length; i++) {
            types[i] = args[i].getClass();
        }
        return types;
    }
}
