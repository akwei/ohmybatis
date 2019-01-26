package com.github.akwei.ohmybatis;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.InitializingBean;

public class OhMyConfiguration extends Configuration implements InitializingBean {

    static OhMyConfiguration configuration;

    private final MapperRegistry mapperRegistry = new OhMyMapperRegistry(this);

    @Override
    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
        Class<?> entityCls = CommonUtils.findParameterizedClassFromMapper(type);
        IMapperFactory.addMapper(entityCls, type);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    @Override
    public void afterPropertiesSet() {
        configuration = this;
    }
}
