package com.github.akwei.ohmybatis;

import java.lang.reflect.Field;
import java.util.Collection;

class CommonUtils {

    static Object getFieldValue(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(
                    "Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }
}
