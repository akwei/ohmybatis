package com.github.akwei.ohmybatis;

import javax.persistence.Transient;

public abstract class BaseEntity<T> implements IEntity<T> {

    @Transient
    private T snapShotObj;

    @Override
    public T getSnapShotObj() {
        return snapShotObj;
    }

    @Override
    public void setSnapShotObj(T snapShotObj) {
        this.snapShotObj = snapShotObj;
    }

}
