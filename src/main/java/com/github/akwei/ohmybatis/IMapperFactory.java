package com.github.akwei.ohmybatis;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SuppressWarnings("unchecked")
public class IMapperFactory implements ApplicationContextAware {

    private static ApplicationContext appctx = null;

    private static final Map<String, Class<?>> imapperMap = new HashMap<>();

    public static void addMapper(Class<?> entityCls, Class<?> mapperCls) {
        imapperMap.put(entityCls.getName(), mapperCls);
    }

    public static <T> IMapper<T> getMapper(Class<?> entityCls) {
        Class<?> aClass = imapperMap.get(entityCls.getName());
        IMapper<T> mapper = (IMapper<T>) appctx.getBean(aClass);
        if (mapper == null) {
            throw new RuntimeException(entityCls.getName() + " must has mapper");
        }
        return mapper;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appctx = applicationContext;
    }
}
