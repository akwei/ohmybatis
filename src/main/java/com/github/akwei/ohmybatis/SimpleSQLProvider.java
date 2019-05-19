package com.github.akwei.ohmybatis;

import com.github.akwei.ohmybatis.annotations.UpdateObj;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Map;
import java.util.Objects;

public class SimpleSQLProvider {

    public static final String INSERT = "buildInsertSQL";
    public static final String UPDATE_OBJ = "buildUpdateObjSQL";
    public static final String DELETE_BYID = "buildDeleteByIdSQL";
    public static final String SELECT_BYID = "buildSelectByIdSQL";

    @SuppressWarnings("unused")
    public static String buildInsertSQL(ProviderContext providerContext,
                                        Map<String, Object> argMap) {
        Class<?>[] parameterTypes = providerContext.getMapperMethod().getParameterTypes();
        Class<?> entityClazz;
        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException(providerContext.getMapperMethod().getName() +
                    "insert method must has only one argument. that is entity object");
        }
        Param param = providerContext.getMapperMethod().getParameters()[0]
                .getAnnotation(Param.class);
        Objects.requireNonNull(param, "method argument must has @param annotation");
        entityClazz = CommonUtils.findParameterizedClassFromMapper(providerContext.getMapperType());
        Options options = providerContext.getMapperMethod().getAnnotation(Options.class);
        EntityInfo entityInfo = EntityInfo.getEntityInfo(entityClazz);
        return entityInfo
                .buildInsertSQL(param.value(), false, false,
                        options.useGeneratedKeys());
    }

    /**
     * 对于 updateObj 方法需要设定第一个参数必须是要更新的对象，第二个参数必须是旧对象，如果更新全对象， 需要设置旧对象为null，之后的参数应该都是 {@link
     * com.github.akwei.ohmybatis.annotations.UpdateObj} 中sql的参数
     *
     * @param providerContext providerContext mybatis ${@link ProviderContext}
     * @param argMap          请求的参数map
     * @return sql
     */
    @SuppressWarnings("unused")
    public static String buildUpdateObjSQL(ProviderContext providerContext,
                                           Map<String, Object> argMap) {
        Object obj = argMap.get("param1");
        Object old = null;
        if (argMap.containsKey("param2")) {
            old = argMap.get("param2");
        }
        Objects.requireNonNull(obj, "update object must be not null");
        Class<?> entityClazz = obj.getClass();
        EntityInfo entityInfo = EntityInfo.getEntityInfo(entityClazz);
        String alias = providerContext.getMapperMethod().getParameters()[0].getName();
        String where = null;
        UpdateObj updateObj = providerContext.getMapperMethod().getAnnotation(UpdateObj.class);
        if (updateObj != null) {
            where = updateObj.where();
        }
        return entityInfo.buildUpdateObjSQL(alias, obj, old, where);
    }

    /**
     * 生成实体的 deleteById 方法
     *
     * @param providerContext providerContext mybatis ${@link ProviderContext}
     * @param argMap          请求的参数map
     * @return sql
     */
    @SuppressWarnings("unused")
    public static String buildDeleteByIdSQL(ProviderContext providerContext,
                                            Map<String, Object> argMap) {
        Object obj = argMap.get("param1");
        Objects.requireNonNull(obj, "deleteById id argument must be not null");
        Class<?> entityClazz = CommonUtils
                .findParameterizedClassFromMapper(providerContext.getMapperType());
        EntityInfo entityInfo = EntityInfo.getEntityInfo(entityClazz);
        return entityInfo.buildDeleteByIdSQL();
    }

    /**
     * 生成实体的 selectById 方法
     *
     * @param providerContext providerContext mybatis ${@link ProviderContext}
     * @param argMap          请求的参数map
     * @return sql
     */
    @SuppressWarnings("unused")
    public static String buildSelectByIdSQL(ProviderContext providerContext,
                                            Map<String, Object> argMap) {
        Object obj1 = argMap.get("param1");
        Boolean forUpdate = null;
        if (argMap.containsKey("param2")) {
            forUpdate = (Boolean) argMap.get("param2");
        }
        Objects.requireNonNull(obj1, "selectById id argument must be not null");
        Class<?> entityClazz = CommonUtils
                .findParameterizedClassFromMapper(providerContext.getMapperType());
        EntityInfo entityInfo = EntityInfo.getEntityInfo(entityClazz);
        return entityInfo.buildSelectByIdSQL(forUpdate);
    }
}
