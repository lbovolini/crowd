package com.github.lbovolini.crowd.common.message.request;


import com.github.lbovolini.crowd.common.object.Service;

import java.lang.reflect.Method;

public class RequestType {
    public static boolean isService(Method method) {
        if (method.isDefault()
                  & method.getDeclaringClass().getName().equals(Service.class.getName())) {
            return true;
        }
        return false;
    }
}
