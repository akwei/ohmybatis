package com.github.akwei.ohmybatis;

import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IMapper<T> {

    /**
     * 执行 insert sql
     *
     * @param t insert的数据
     */
    @Options(keyProperty = "[id]")//使用[id]表示动态的获取实体对象的id属性
    @InsertProvider(type = SimpleSQLProvider.class, method = SimpleSQLProvider.INSERT)
    void insert(@Param("obj") T t);

    @Lang(OhMyXmlDriver.class)
    @Options(keyProperty = "[id]")//使用[id]表示动态的获取实体对象的id属性
    @Insert(SQL.BATCH_INSERT)
    void insertBatch(@Param("list") List<T> list);

    /**
     * 执行 update sql，生成的sql为变化过的字段
     *
     * @param t        update的数据
     * @param snapshot 数据未更新前的副本, null 表示更新除了id的所有字段
     * @return 操作结果
     */
    @UpdateProvider(type = SimpleSQLProvider.class, method = SimpleSQLProvider.UPDATE_OBJ)
    int update(T t, T snapshot);

    /**
     * 根据id删除数据
     *
     * @param id   数据的id
     * @param <PK> d 类型
     * @return 操作结果
     */
    @DeleteProvider(type = SimpleSQLProvider.class, method = SimpleSQLProvider.DELETE_BYID)
    <PK> int deleteById(@Param("id") PK id);

    /**
     * 根据id查询数据
     *
     * @param id        数据的id
     * @param forUpdate 是否使用 for update sql
     * @param <PK>      id 类型
     * @return 查询结果
     */
    @SelectProvider(type = SimpleSQLProvider.class, method = SimpleSQLProvider.SELECT_BYID)
    <PK> T selectById(@Param("id") PK id, boolean forUpdate);

    @Lang(OhMyXmlDriver.class)
    @Select({SQL.SELECT_IN_IDS,
            "where userid $in{userids} order by createtime desc limit #{offset} , #{size}"})
    <PK> List<T> selectInIds(@Param("ids") List<PK> ids);
}
