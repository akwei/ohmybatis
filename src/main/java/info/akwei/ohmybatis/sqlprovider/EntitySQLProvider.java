package info.akwei.ohmybatis.sqlprovider;

import info.akwei.ohmybatis.annotations.AfterWhere;
import info.akwei.ohmybatis.mapper.BaseMapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;

import javax.persistence.Table;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EntitySQLProvider {

    public static final String INSERT = "buildInsertSQL";

    public static final String UPDATE_OBJ = "buildUpdateObjSQL";

    public static final String UPDATE = "buildUpdateSQL";

    public static final String DELETE = "buildDeleteSQL";

    public static final String SELECT = "buildSelectSQL";

    public static final String COUNT = "buildCountSQL";

    private static Map<String, EntityInfo> map = new ConcurrentHashMap<>();

    private static synchronized EntityInfo getEntityInfo(Class clazz) {
        EntityInfo entityInfo = map.get(clazz.getName());
        if (entityInfo == null) {
            entityInfo = new EntityInfo(clazz, false);
            entityInfo.init();
            map.put(clazz.getName(), entityInfo);
        }
        return entityInfo;
    }

    @SuppressWarnings("unused") //某些代码自动调用
    public static String buildInsertSQL(ProviderContext providerContext) {
        Class<?>[] parameterTypes = providerContext.getMapperMethod().getParameterTypes();
        Class<?> entityClazz;
        if (parameterTypes == null || parameterTypes.length != 1) {
            throw new IllegalArgumentException(providerContext.getMapperMethod().getName() +
                    "insert method must has only one argument. that is entity object");
        }
        entityClazz = parameterTypes[0];
        Options options = providerContext.getMapperMethod().getAnnotation(Options.class);
        EntityInfo entityInfo = getEntityInfo(entityClazz);
        Table table = entityClazz.getAnnotation(Table.class);
        Objects.requireNonNull(table.name(), entityClazz.getName() + " table name must be not empty");
        return entityInfo.buildInsertSQL(table.name(), options.useGeneratedKeys());
    }

    @SuppressWarnings("unused") //某些代码自动调用
    public static String buildUpdateObjSQL(ProviderContext providerContext, Map<String, Object> argMap) {
        Object obj = argMap.get("param1");
        Object old = null;
        if (argMap.containsKey("param2")) {
            old = argMap.get("param2");
        }
        Objects.requireNonNull(obj, "update object must be not null");
        Class<?> entityClazz = obj.getClass();
        EntityInfo entityInfo = getEntityInfo(entityClazz);
        Table table = entityClazz.getAnnotation(Table.class);
        Objects.requireNonNull(table.name(), entityClazz.getName() + " table name must be not empty");
        String alias = providerContext.getMapperMethod().getParameters()[0].getName();
        return entityInfo.buildUpdateObjSQL(table.name(), alias, obj, old);
    }

    @SuppressWarnings("unused") //某些代码自动调用
    public static String buildUpdateSQL(ProviderContext providerContext, Map<String, Object> argMap) {
        Class<?> entityClazz;
        Object param1 = argMap.get("param1");
        int begin = 0;
        if (param1 instanceof Class) {
            begin = 1;
            entityClazz = (Class<?>) param1;
        } else {
            entityClazz = findParameterizedClass(providerContext.getMapperType());
        }
        if (entityClazz == null) {
            throw new NullPointerException();
        }

        EntityInfo entityInfo = getEntityInfo(entityClazz);
        Table table = entityClazz.getAnnotation(Table.class);
        Objects.requireNonNull(table.name(), entityClazz.getName() + " table name must be not empty");
        Param param = providerContext.getMapperMethod().getParameters()[0].getAnnotation(Param.class);
        Objects.requireNonNull(param.value(), "update argument must has @param annotation");
        Parameter[] parameters = providerContext.getMapperMethod().getParameters();
        List<Parameter> params = new ArrayList<>();
        int i = begin;
        while (i < parameters.length) {
            params.add(parameters[i]);
            i++;
        }
        return entityInfo.buildUpdateSQL(table.name(), params);
    }


    @SuppressWarnings("unused") //某些代码自动调用
    public static String buildDeleteSQL(ProviderContext providerContext, Map<String, Object> argMap) {
        Class<?> entityClazz;
        Object param1 = argMap.get("param1");
        int begin = 0;
        if (param1 instanceof Class) {
            begin = 1;
            entityClazz = (Class<?>) param1;
        } else {
            entityClazz = findParameterizedClass(providerContext.getMapperType());
        }
        if (entityClazz == null) {
            throw new NullPointerException();
        }
        Table table = entityClazz.getAnnotation(Table.class);
        Objects.requireNonNull(table.name(), entityClazz.getName() + " table name must be not empty");
        EntityInfo entityInfo = getEntityInfo(entityClazz);
        Parameter[] parameters = providerContext.getMapperMethod().getParameters();
        List<Parameter> params = new ArrayList<>();
        int i = begin;
        while (i < parameters.length) {
            params.add(parameters[i]);
            i++;
        }
        return entityInfo.buildDeleteSQL(table.name(), params);
    }

    @SuppressWarnings("unused") //某些代码自动调用
    public static String buildSelectSQL(ProviderContext providerContext, Map<String, Object> argMap) {
        Class<?> entityClazz;
        Object param1 = argMap.get("param1");
        int begin = 0;
        if (param1 instanceof Class) {
            begin = 1;
            entityClazz = (Class<?>) param1;
        } else {
            entityClazz = findParameterizedClass(providerContext.getMapperType());
        }
        if (entityClazz == null) {
            throw new NullPointerException();
        }
        Table table = entityClazz.getAnnotation(Table.class);
        Objects.requireNonNull(table.name(), entityClazz.getName() + " table name must be not empty");
        EntityInfo entityInfo = getEntityInfo(entityClazz);
        Parameter[] parameters = providerContext.getMapperMethod().getParameters();
        List<Parameter> params = new ArrayList<>();
        int i = begin;
        while (i < parameters.length) {
            params.add(parameters[i]);
            i++;
        }
        AfterWhere afterWhere = providerContext.getMapperMethod().getAnnotation(AfterWhere.class);
        String afterWhereSub = null;
        if (afterWhere != null) {
            afterWhereSub = afterWhere.value();
        }
        return entityInfo.buildSelectSQL(table.name(), params, afterWhereSub);
    }

    @SuppressWarnings("unused") //某些代码自动调用
    public static String buildCountSQL(ProviderContext providerContext, Map<String, Object> argMap) {
        Class<?> entityClazz;
        Object param1 = argMap.get("param1");
        int begin = 0;
        if (param1 instanceof Class) {
            begin = 1;
            entityClazz = (Class<?>) param1;
        } else {
            entityClazz = findParameterizedClass(providerContext.getMapperType());
        }
        if (entityClazz == null) {
            throw new NullPointerException();
        }
        Table table = entityClazz.getAnnotation(Table.class);
        Objects.requireNonNull(table.name(), entityClazz.getName() + " table name must be not empty");
        EntityInfo entityInfo = getEntityInfo(entityClazz);
        Parameter[] parameters = providerContext.getMapperMethod().getParameters();
        List<Parameter> params = new ArrayList<>();
        int i = begin;
        while (i < parameters.length) {
            params.add(parameters[i]);
            i++;
        }
        AfterWhere afterWhere = providerContext.getMapperMethod().getAnnotation(AfterWhere.class);
        String afterWhereSub = null;
        if (afterWhere != null) {
            afterWhereSub = afterWhere.value();
        }
        return entityInfo.buildCountSQL(table.name(), params, afterWhereSub);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> findParameterizedClass(Class clazz) {
        ParameterizedType type = null;
        for (Type iface : clazz.getGenericInterfaces()) {
            ParameterizedType otype = (ParameterizedType) iface;
            if (otype.getRawType().equals(BaseMapper.class)) {
                type = otype;
                break;
            }
        }
        if (type == null) {
            return null;
        }
        Type[] types = type.getActualTypeArguments();
        if (types == null || types.length == 0) {
            return null;
        }
        return (Class) types[0];
    }

//    public static SqlCommandType getSqlCommandType(Method method) {
//        String methodName = method.getName();
//        if (methodName.startsWith("insert")) {
//            return SqlCommandType.INSERT;
//        }
//        if (methodName.startsWith("updateObj")) {
//            return SqlCommandType.UPDATE;
//        }
//        if (methodName.startsWith("update")) {
//            return SqlCommandType.UPDATE;
//        }
//        if (methodName.startsWith("delete")) {
//            return SqlCommandType.DELETE;
//        }
//        if (methodName.startsWith("select") || methodName.startsWith("find") || methodName.startsWith("query") || methodName.startsWith("count")) {
//            return SqlCommandType.SELECT;
//        }
//        return SqlCommandType.UNKNOWN;
//    }
//
//
//    public static String buildSQL(ProviderContext providerContext, Map<String, Object> argMap) {
//        String methodName = providerContext.getMapperMethod().getName();
//        if (methodName.startsWith("insert")) {
//            return EntitySQLProvider.buildInsertSQL(providerContext);
//        }
//        if (methodName.startsWith("updateObj")) {
//            return EntitySQLProvider.buildUpdateObjSQL(providerContext, argMap);
//        }
//        if (methodName.startsWith("update")) {
//            return EntitySQLProvider.buildUpdateSQL(providerContext, argMap);
//        }
//        if (methodName.startsWith("delete")) {
//            return EntitySQLProvider.buildDeleteSQL(providerContext, argMap);
//        }
//        if (methodName.startsWith("select") || methodName.startsWith("find") || methodName.startsWith("query")) {
//            return EntitySQLProvider.buildSelectSQL(providerContext, argMap);
//        }
//        if (methodName.startsWith("count")) {
//            return EntitySQLProvider.buildCountSQL(providerContext, argMap);
//        }
//        throw new RuntimeException("methodName:" + methodName + " can not matched : insert, updateObj, update, delete, select, find, query, count");
//    }
}
