package com.github.lbovolini.crowd;

import com.github.lbovolini.crowd.connection.Connection;
import com.github.lbovolini.crowd.message.Message;
import com.github.lbovolini.crowd.message.MessageFactory;
import com.github.lbovolini.crowd.message.messages.InvokeMethod;

import java.io.IOException;
import java.lang.reflect.Method;

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
        try {
            Message message = MessageFactory.reply(requestId, result, exception);
            connection.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getException(Object object, InvokeMethod request){
        String className = object != null ? object.getClass().getName() : "null";
        String methodName = request != null ? request.getMethod() : "null";
        return  "Servant execute error on class: " + className + " method: " + methodName;
    }

//    private static Map<Class, Class> PRIMITIVE_MAP;
//
//    static {
//        Map<Class, Class> map = new ConcurrentHashMap<>();
//
//        map.put(byte.class, Byte.TYPE);
//        map.put(char.class, Character.TYPE);
//        map.put(short.class, Short.TYPE);
//        map.put(int.class, Integer.TYPE);
//        map.put(long.class, Long.TYPE);
//        map.put(float.class, Float.TYPE);
//        map.put(double.class, Double.TYPE);
//        map.put(boolean.class, Boolean.TYPE);
//
//        PRIMITIVE_MAP = Collections.unmodifiableMap(map);
//    }
//
//    private static Class<?>[] resolvePrimitiveTypes(Class<?>[] parameterTypes) {
//        Class<?>[] p = new Class[parameterTypes.length];
//
//        for (int i = 0; i < parameterTypes.length; i++) {
//            Class<?> type = PRIMITIVE_MAP.get(parameterTypes[i]);
//            System.out.println("TYPE " + type);
//            if (type != null) {
//                p[i] = type;
//            } else {
//                p[i] = parameterTypes[i];
//            }
//        }
//
//        return p;
//    }

}
