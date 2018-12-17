package com.github.lbovolini.crowd;

import com.github.lbovolini.crowd.connection.Connection;
import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.MessageFactory;
import com.github.lbovolini.crowd.message.messages.InvokeMethod;

import java.io.IOException;

public class Servant {

    public static void execute(final Object object, InvokeMethod request, Connection connection) {
        Object result = null;
        String exception = null;

        try {
            Object[] args = request.getArgs();
            Class[] types = getTypes(args);
            result = object.getClass().getMethod(request.getMethod(),
                    types).invoke(object, request.getArgs());
        } catch (Exception ex) {
            exception = getException(object, request);
            //ex.printStackTrace();
        }

        reply(request.getRequestId(), result, exception, connection);

    }

    private static void reply(int requestId, Object result, String exception, Connection connection) {
        try {
            Message message = MessageFactory.reply(requestId, result, exception);
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

    private static String getException(Object object, InvokeMethod request){
        String className = object != null ? object.getClass().getName() : "null";
        String methodName = request != null ? request.getMethod() : "null";
        return  "Servant execute error on class: " + className + " method: " + methodName;
    }
}
