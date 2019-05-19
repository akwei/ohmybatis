package com.github.akwei.ohmybatis;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.Map;

public class OhMyMapperRegistry extends MapperRegistry {

    static Class<?> curMapperCls;

    private Configuration config;

    private Map<Class<?>, MapperProxyFactory<?>> prvKnownMappers;

    @SuppressWarnings("unchecked")
    OhMyMapperRegistry(Configuration config) {
        super(config);
        this.config = config;
        try {
            Field knownMappers = this.getClass().getSuperclass().getDeclaredField("knownMappers");
            knownMappers.setAccessible(true);
            prvKnownMappers = (Map<Class<?>, MapperProxyFactory<?>>) knownMappers.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        curMapperCls = type;
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new BindingException(
                        "Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                prvKnownMappers.put(type, new MapperProxyFactory<>(type));
                // It's important that the type is added before the parser is run
                // otherwise the binding may automatically be attempted by the
                // mapper parser. If the type is already known, it won't try.
                OhMyMapperAnnotationBuilder parser = new OhMyMapperAnnotationBuilder(config, type);
                parser.parse();
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    prvKnownMappers.remove(type);
                }
            }
        }
    }
}
