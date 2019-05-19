package com.github.akwei.ohmybatis;

import javax.persistence.Transient;

@SuppressWarnings("unchecked")
public abstract class BaseEntity<T> implements IEntity<T> {

    @Transient
    private T _snapshotObj;

    /**
     * 创建当前对象的快照数据
     */
    public void localSnapshot() {
        this._snapshotObj = this.snapshot();
    }

    public void insert() {
        IMapper<T> mapper = IMapperFactory.getMapper(this.getClass());
        mapper.insert((T) this);
    }

    public int update() {
        IMapper<T> mapper = IMapperFactory.getMapper(this.getClass());
        return mapper.update((T) this, this._snapshotObj);
    }

    public int delete() {
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
