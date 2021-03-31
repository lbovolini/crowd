package com.github.lbovolini.crowd.client.runner;

import com.github.lbovolini.crowd.core.connection.Connection;
import com.github.lbovolini.crowd.core.message.Message;
import com.github.lbovolini.crowd.core.message.MessageFactory;
import com.github.lbovolini.crowd.core.message.messages.InvokeMethod;

import java.lang.reflect.Method;

/**
 * Responsável pela execução dos métodos no objeto remoto hospedado.
 */
public class Servant {

    public static void execute(final Object object, InvokeMethod request, Connection connection) {

        Object result = null;
        String exception = null;
        boolean isPrimitiveVoid = true;

        try {
            Class<?>[] parameterTypes = request.getParameterTypes();
            Method method = object.getClass().getMethod(request.getMethod(), parameterTypes);
            isPrimitiveVoid = Void.TYPE.equals(method.getReturnType());

            result = method.invoke(object, request.getArgs());
        } catch (Exception ex) {
            exception = getException(object, request);
        }

        if (!isPrimitiveVoid) {
            reply(request.getRequestId(), result, exception, connection);
        }

    }

    private static void reply(int requestId, Object result, String exception, Connection connection) {
        Message message = MessageFactory.reply(requestId, result, exception);
        connection.send(message);
    }

    private static String getException(Object object, InvokeMethod request){
        String className = object != null ? object.getClass().getName() : "null";
        String methodName = request != null ? request.getMethod() : "null";
        return  "Servant execute error on class: " + className + " method: " + methodName;
    }

}
