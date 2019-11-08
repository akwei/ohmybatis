package com.github.akwei.ohmybatis;

import java.lang.reflect.Constructor;

@SuppressWarnings("unchecked")
public interface IEntity<T> {

    T getSnapShotObj();

    void setSnapShotObj(T snapShotObj);

    @SuppressWarnings("unchecked")
    default void snapshot() {
        try {
            T t = (T) this;
            Constructor<T> constructor = (Constructor<T>) t.getClass().getConstructor();
            T snapshotObj = constructor.newInstance();
            EntityCopier.copy(t, snapshotObj);
            this.setSnapShotObj(snapshotObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default void insert() {
        IMapper<T> mapper = IMapperFactory.getMapper(this.getClass());
        mapper.insert((T) this);
    }

    default int update() {
        IMapper<T> mapper = IMapperFactory.getMapper(this.getClass());
        return mapper.update((T) this, this.getSnapShotObj());
    }

    default int delete() {
        IMapper<T> mapper = IMapperFactory.getMapper(this.getClass());
        EntityInfo entityInfo = EntityInfo.getEntityInfo(this.getClass());
        FieldInfo idFieldInfo = entityInfo.getIdFieldInfo();
        if (idFieldInfo == null) {
            throw new RuntimeException(
                    this.getClass().getName() + " invoke delete method but has not @Id");
        }
        try {
            Object idValue = idFieldInfo.getField().get(this);
            return mapper.deleteById(idValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
