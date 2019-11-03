package com.github.akwei.ohmybatis;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

class EntityInfo {

    private boolean mapUnderscoreToCamelCase;

    /**
     * mapping with table's column
     */
    private List<FieldInfo> fieldInfos;

    private FieldInfo idFieldInfo;

    private String tableName;

    private static Map<String, EntityInfo> map = new ConcurrentHashMap<>();

    private EntityInfo(boolean mapUnderscoreToCamelCase) {
        this.fieldInfos = new ArrayList<>();
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;

    }

    static synchronized EntityInfo getEntityInfo(Class<?> clazz) {
        EntityInfo entityInfo = map.get(clazz.getName());
        if (entityInfo == null) {
            Table entity = clazz.getAnnotation(Table.class);
            if (entity == null) {
                throw new RuntimeException(
                        clazz.getName() + " must set annotation " + Table.class.getName());
            }
            entityInfo = new EntityInfo(
                    OhMyConfiguration.configuration.isMapUnderscoreToCamelCase());
            entityInfo.tableName = entity.name();
            entityInfo.buildFieldsForClass(clazz);
            map.put(clazz.getName(), entityInfo);
        }
        return entityInfo;
    }

    FieldInfo getIdFieldInfo() {
        return idFieldInfo;
    }

    private void buildFieldsForClass(Class<?> clazz) {
        Class<?> curClazz = clazz;
        while (curClazz != null) {
            Field[] fs = curClazz.getDeclaredFields();
            for (Field f : fs) {
                if (!f.getDeclaringClass().equals(clazz)) {
                    continue;
                }
                f.setAccessible(true);
                Transient aTransient = f.getAnnotation(Transient.class);
                if (aTransient != null) {
                    continue;
                }
                FieldInfo fieldInfo = new FieldInfo(f, this.mapUnderscoreToCamelCase);
                this.fieldInfos.add(fieldInfo);
                if (f.getAnnotation(Id.class) != null) {
                    this.idFieldInfo = fieldInfo;
                }
            }
            curClazz = curClazz.getSuperclass();
            if (curClazz.equals(Object.class)) {
                curClazz = null;
            }
        }
    }

    String buildInsertSQL(String paramName, boolean script, boolean batch,
                          boolean genKey) {
        StringBuilder sb = new StringBuilder();
        if (script) {
            sb.append("<script>\n");
        }
        sb.append("insert into ");
        sb.append(tableName);
        sb.append('(');
        List<FieldInfo> list = new ArrayList<>(this.fieldInfos);
        if (genKey) {
            list.remove(this.idFieldInfo);
        }
        int k = 0;
        int lastIdx = list.size() - 1;
        for (FieldInfo fieldInfo : list) {
            sb.append(fieldInfo.getColumn());
            if (k < lastIdx) {
                sb.append(",");
            }
            k++;
        }
        sb.append(") values ");
        if (batch) {
            sb.append("\n<foreach item=\"item\" collection=\"list\" separator=\",\">\n");
            paramName = "item";
        }
        sb.append('(');
        k = 0;
        for (FieldInfo fieldInfo : list) {
            sb.append("#{").append(paramName).append('.');
            sb.append(fieldInfo.getField().getName());
            sb.append('}');
            if (k < lastIdx) {
                sb.append(",");
            }
            k++;
        }
        sb.append(')');
        if (batch) {
            sb.append("\n</foreach>");
        }
        if (script) {
            sb.append("\n</script>");
        }
        return sb.toString();
    }

    <T> String buildUpdateObjSQL(String alias, T t, T old) {
        if (old == null) {
            return _buildUpdateObj(alias, this.fieldInfos);
        }
        return _buildUpdateSQL4Old(alias, t, old);
    }

    private <T> String _buildUpdateSQL4Old(String alias, T t, T old) {
        List<FieldInfo> list = new ArrayList<>();
        for (FieldInfo fieldInfo : this.fieldInfos) {
            Object valueT = CommonUtils.getFieldValue(fieldInfo.getField(), t);
            Object valueOld = CommonUtils.getFieldValue(fieldInfo.getField(), old);
            if (Objects.equals(valueT, valueOld)) {
                continue;
            }
            list.add(fieldInfo);
        }
        return _buildUpdateObj(alias, list);
    }

    private String _buildUpdateObj(String alias, List<FieldInfo> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(tableName);
        sb.append(" set ");
        int k = 0;
        int lastIdx = list.size() - 1;
        for (FieldInfo fieldInfo : list) {
            if (!fieldInfo.isId()) {
                sb.append(fieldInfo.getColumn()).append("=").append("#{").append(alias).append(".")
                        .append(fieldInfo.getField().getName()).append('}');
                if (k < lastIdx) {
                    sb.append(", ");
                }
            }
            k++;
        }
        if (this.idFieldInfo != null) {
            sb.append(" where ");
            this.appendFieldInfoToSQLWhere(sb, alias, this.idFieldInfo);
        }
        return sb.toString();
    }

    String buildDeleteByIdSQL() {
        return "delete from " + this.tableName + " where " +
                this.idFieldInfo.getColumn() + "=" + "#{id}";
    }

    String buildSelectByIdSQL(Boolean forUpdate) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(this.tableName).append(" where ")
                .append(this.idFieldInfo.getColumn()).append("=").append("#{id}");
        if (forUpdate != null && forUpdate) {
            sb.append(" for update");
        }
        return sb.toString();
    }

    private void appendFieldInfoToSQLWhere(StringBuilder sb, String alias, FieldInfo fieldInfo) {
        sb.append(fieldInfo.getColumn()).append("=").append("#{")
                .append(alias).append(".").append(fieldInfo.getField().getName())
                .append("}");
    }

    boolean isGenKey() {
        FieldInfo idFieldInfo = this.getIdFieldInfo();
        boolean genKey = false;
        if (idFieldInfo != null) {
            GeneratedValue generatedValue = idFieldInfo.getField()
                    .getAnnotation(GeneratedValue.class);
            if (generatedValue != null && generatedValue.strategy()
                    .equals(GenerationType.IDENTITY)) {
                genKey = true;
            }
        }
        return genKey;
    }


    String getTableName() {
        return tableName;
    }
}