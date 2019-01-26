package com.github.akwei.ohmybatis;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

class CommonUtils {

    static Object getFieldValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(
                  "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex
                        .getMessage());
        }
    }

    static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    static Class<?> findParameterizedClassFromMapper(Class clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (genericInterfaces == null || genericInterfaces.length == 0) {
            throw new RuntimeException(clazz.getName() + " must has mapper<T>");
        }
        ParameterizedType type = (ParameterizedType) genericInterfaces[0];
        if (type == null) {
            throw new RuntimeException(clazz.getName() + " must has mapper<T>");
        }
        Type[] types = type.getActualTypeArguments();
        if (types == null || types.length == 0) {
            throw new RuntimeException(clazz.getName() + " must has mapper<T>");
        }
        return (Class<?>) types[0];
    }
}
