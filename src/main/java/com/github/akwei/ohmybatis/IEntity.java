package com.github.akwei.ohmybatis;

import java.lang.reflect.Constructor;

public interface IEntity<T> {

    @SuppressWarnings("unchecked")
    default T snapshot() {
        try {
            T t = (T) this;
            Constructor<T> constructor = (Constructor<T>) t.getClass().getConstructor();
            T snapshotObj = constructor.newInstance();
            EntityCopier.copy(t, snapshotObj);
            return snapshotObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
