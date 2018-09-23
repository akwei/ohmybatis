package com.github.akwei.ohmybatis;

import com.github.akwei.ohmybatis.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

class EntityInfo {

    private Class clazz;

    private boolean mapUnderscoreToCamelCase;

    private List<FieldInfo> idFieldInfos;

    private Map<String, FieldInfo> fieldInfoMap;


    /**
     * mapping with table's column
     */
    private List<FieldInfo> fieldInfos;

    EntityInfo(Class clazz, boolean mapUnderscoreToCamelCase) {
        this.fieldInfos = new ArrayList<>();
        this.idFieldInfos = new ArrayList<>();
        this.fieldInfoMap = new HashMap<>();
        this.clazz = clazz;
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    void init() {
        this.buildFieldsForClass(clazz);
    }

    private void buildFieldsForClass(Class clazz) {
        Objects.requireNonNull(clazz, "clazz must be not null");
        Class curClazz = clazz;
        while (curClazz != null) {
            Field[] fs = clazz.getDeclaredFields();
            for (Field f : fs) {
                if (!f.getDeclaringClass().equals(clazz)) {
                    continue;
                }
                f.setAccessible(true);
                NotColumn notColumn = f.getAnnotation(NotColumn.class);
                if (notColumn != null) {
                    continue;
                }
                FieldInfo fieldInfo = new FieldInfo(f, this.mapUnderscoreToCamelCase);
                Id id = f.getAnnotation(Id.class);
                if (id != null) {
                    idFieldInfos.add(fieldInfo);
                } else {
                    this.fieldInfos.add(fieldInfo);
                }
                fieldInfoMap.put(f.getName(), fieldInfo);
            }
            curClazz = curClazz.getSuperclass();
            if (curClazz.equals(Object.class)) {
                curClazz = null;
            }
        }
    }

    String buildInsertSQL(String tableName, boolean genKey) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(tableName);
        sb.append('(');
        List<FieldInfo> list = new ArrayList<>();
        if (!genKey) {
            list.addAll(this.idFieldInfos);
        }
        list.addAll(this.fieldInfos);

        int k = 0;
        int lastIdx = list.size() - 1;
        for (FieldInfo fieldInfo : list) {
            sb.append(fieldInfo.getColumn());
            if (k < lastIdx) {
                sb.append(",");
            }
            k++;
        }
        sb.append(") values (");
        k = 0;
        for (FieldInfo fieldInfo : list) {
            sb.append("#{");
            sb.append(fieldInfo.getField().getName());
            sb.append('}');
            if (k < lastIdx) {
                sb.append(",");
            }
            k++;
        }
        sb.append(')');
        return sb.toString();
    }

    <T> String buildUpdateObjSQL(String tableName, String alias, T t, T old) {
        if (old == null) {
            return _buildUpdateObj(tableName, alias, this.fieldInfos);
        }
        return _buildUpdateSQL4Old(tableName, alias, t, old);
    }

    private <T> String _buildUpdateSQL4Old(String tableName, String alias, T t, T old) {
        List<FieldInfo> list = new ArrayList<>();
        for (FieldInfo fieldInfo : this.fieldInfos) {
            Object valueT = CommonUtils.getFieldValue(fieldInfo.getField(), t);
            Object valueOld = CommonUtils.getFieldValue(fieldInfo.getField(), old);
            if (Objects.equals(valueT, valueOld)) {
                continue;
            }
            list.add(fieldInfo);
        }
        return _buildUpdateObj(tableName, alias, list);
    }

    private String _buildUpdateObj(String tableName, String alias, List<FieldInfo> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(tableName);
        sb.append(" set ");
        int k = 0;
        int lastIdx = list.size() - 1;
        for (FieldInfo fieldInfo : list) {
            sb.append(fieldInfo.getColumn()).append("=").append("#{").append(alias).append(".").append(fieldInfo.getField().getName()).append('}');
            if (k < lastIdx) {
                sb.append(", ");
            }
            k++;
        }

        sb.append(" where ");
        k = 0;
        lastIdx = this.idFieldInfos.size() - 1;
        for (FieldInfo idFieldInfo : this.idFieldInfos) {
            sb.append(idFieldInfo.getColumn()).append("=").append("#{").append(alias).append(".").append(idFieldInfo.getField().getName()).append('}');
            if (k < lastIdx) {
                sb.append(" and ");
            }
            k++;
        }
        return sb.toString();
    }

    String buildDeleteSQL(String tableName, List<Parameter> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ").append(tableName);
        if (CommonUtils.isEmpty(parameters)) {
            return sb.toString();
        }
        this.buildWhereStringBuffer(sb, parameters);
        return sb.toString();
    }

    String buildUpdateSQL(String tableName, List<Parameter> parameters) {
        List<Parameter> valueParameters = new ArrayList<>();
        List<Parameter> whereParameters = new ArrayList<>();
        for (Parameter parameter : parameters) {
            UpdateField updateField = parameter.getAnnotation(UpdateField.class);
            if (updateField != null) {
                valueParameters.add(parameter);
            } else {
                whereParameters.add(parameter);
            }
        }

        if (CommonUtils.isEmpty(valueParameters)) {
            throw new IllegalArgumentException("update sql must has set ...");
        }
        if (CommonUtils.isEmpty(whereParameters)) {
            throw new IllegalArgumentException("update sql must has where ...");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(tableName);
        sb.append(" set ");

        int i = 0;
        int lastIdx = valueParameters.size() - 1;
        for (Parameter valueParameter : valueParameters) {
            FieldInfo fieldInfo = this.fieldInfoMap.get(valueParameter.getName());
            if (fieldInfo == null) {
                throw new NullPointerException(valueParameter.getName() + " can not find fieldInfo in map");
            }
            sb.append(fieldInfo.getColumn()).append("=").append("#{").append(valueParameter.getName()).append("}");
            if (i < lastIdx) {
                sb.append(", ");
            }
        }
        this.buildWhereStringBuffer(sb, parameters);
        return sb.toString();
    }

    String buildSelectSQL(String tableName, List<Parameter> parameters, String afterWhere, boolean forUpdate) {
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(tableName);
        if (CommonUtils.isEmpty(parameters)) {
            return sb.toString();
        }
        this.buildWhereStringBuffer(sb, parameters);
        if (afterWhere != null) {
            sb.append(" ").append(afterWhere);
        }
        if (forUpdate) {
            sb.append(" for update");
        }
        return sb.toString();
    }

    String buildCountSQL(String tableName, List<Parameter> parameters, String afterWhere) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from ").append(tableName);
        if (CommonUtils.isEmpty(parameters)) {
            return sb.toString();
        }
        this.buildWhereStringBuffer(sb, parameters);
        if (afterWhere != null) {
            sb.append(" ").append(afterWhere);
        }
        return sb.toString();
    }

    private void buildWhereStringBuffer(StringBuilder sb, List<Parameter> parameters) {
        sb.append(" where ");
        List<Parameter> whereParameters = new ArrayList<>();
        for (Parameter parameter : parameters) {
            NotColumn notColumn = parameter.getAnnotation(NotColumn.class);
            if (notColumn != null) {
                continue;
            }
            if (parameter.getName().equalsIgnoreCase("forUpdate")) {
                continue;
            }
            whereParameters.add(parameter);
        }

        int i = 0;
        int lastIdx = whereParameters.size() - 1;
        for (Parameter parameter : whereParameters) {
            MaxValue maxValue = parameter.getAnnotation(MaxValue.class);
            if (maxValue != null) {
                String fieldName;
                if (StringUtils.isNotEmpty(maxValue.value())) {
                    fieldName = maxValue.value();
                } else {
                    fieldName = parameter.getName();
                }
                FieldInfo fieldInfo = this.fieldInfoMap.get(fieldName);
                if (fieldInfo == null) {
                    throw new NullPointerException(maxValue.value() + " can not find fieldInfo in map");
                }
                sb.append(fieldInfo.getColumn());
                sb.append("<");
                if (maxValue.include()) {
                    sb.append("=");
                }
                this.buildParameterSb(parameter, sb);
                if (i < lastIdx) {
                    sb.append(" and ");
                }
                i++;
                continue;
            }
            MinValue minValue = parameter.getAnnotation(MinValue.class);
            if (minValue != null) {
                String fieldName;
                if (StringUtils.isNotEmpty(minValue.value())) {
                    fieldName = minValue.value();
                } else {
                    fieldName = parameter.getName();
                }
                FieldInfo fieldInfo = this.fieldInfoMap.get(fieldName);
                if (fieldInfo == null) {
                    throw new NullPointerException(minValue.value() + " can not find fieldInfo in map");
                }
                sb.append(fieldInfo.getColumn());
                sb.append(">");
                if (minValue.include()) {
                    sb.append("=");
                }
                this.buildParameterSb(parameter, sb);
                if (i < lastIdx) {
                    sb.append(" and ");
                }
                i++;
                continue;
            }
            NotEq notEq = parameter.getAnnotation(NotEq.class);
            if (notEq != null) {
                String fieldName;
                if (StringUtils.isNotEmpty(notEq.value())) {
                    fieldName = notEq.value();
                } else {
                    fieldName = parameter.getName();
                }
                FieldInfo fieldInfo = this.fieldInfoMap.get(fieldName);
                if (fieldInfo == null) {
                    throw new NullPointerException(notEq.value() + " can not find fieldInfo in map");
                }
                sb.append(fieldInfo.getColumn());
                sb.append("!=");
                this.buildParameterSb(parameter, sb);
                if (i < lastIdx) {
                    sb.append(" and ");
                }
                i++;
                continue;
            }
            Like like = parameter.getAnnotation(Like.class);
            if (like != null) {
                String fieldName;
                if (StringUtils.isNotEmpty(like.value())) {
                    fieldName = like.value();
                } else {
                    fieldName = parameter.getName();
                }
                FieldInfo fieldInfo = this.fieldInfoMap.get(fieldName);
                if (fieldInfo == null) {
                    throw new NullPointerException(like.value() + " can not find fieldInfo in map");
                }
                sb.append(fieldInfo.getColumn());
                sb.append(" like ");
                if (like.left()) {
                    sb.append("\"%\"");
                }
                this.buildParameterSb(parameter, sb);
                if (like.right()) {
                    sb.append("\"%\"");
                }
                if (i < lastIdx) {
                    sb.append(" and ");
                }
                i++;
                continue;
            }
            //not above condition then append equal expression
            String fieldName = parameter.getName();
            FieldInfo fieldInfo = this.fieldInfoMap.get(fieldName);
            if (fieldInfo == null) {
                throw new NullPointerException(fieldName + " can not find fieldInfo in map");
            }
            sb.append(fieldInfo.getColumn()).append("=");
            this.buildParameterSb(parameter, sb);
            if (i < lastIdx) {
                sb.append(" and ");
            }
            i++;
        }
    }

    private void buildParameterSb(Parameter parameter, StringBuilder sb) {
        Param param = parameter.getAnnotation(Param.class);
        sb.append("#{");
        if (param != null) {
            sb.append(param.value());
        } else {
            sb.append(parameter.getName());
        }
        sb.append("}");
    }


}