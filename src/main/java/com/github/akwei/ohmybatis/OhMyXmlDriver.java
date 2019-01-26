package com.github.akwei.ohmybatis;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

public class OhMyXmlDriver extends XMLLanguageDriver {

    private static final String IN_BEGIN = "$in{";
    private static final String IN_END = "}";


    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement,
          Object parameterObject, BoundSql boundSql) {
        return super.createParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script,
          Class<?> parameterType) {
        return super.createSqlSource(configuration, script, parameterType);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script,
          Class<?> parameterType) {
        Class<?> clazz = CommonUtils
              .findParameterizedClassFromMapper(OhMyMapperRegistry.curMapperCls);
        EntityInfo entityInfo = EntityInfo.getEntityInfo(clazz);
        String sql = null;
        if (script.startsWith(SQL.INSERT)) {
            sql = entityInfo.buildInsertSQL(null, true, false, entityInfo.isGenKey());
        }
        if (script.startsWith(SQL.BATCH_INSERT)) {
            sql = entityInfo.buildInsertSQL(null, true, true, entityInfo.isGenKey());
        } else if (script.startsWith(SQL.SELECT_IN_IDS)) {
            sql = buildSelectInIds(entityInfo);
        } else if (script.startsWith(SQL.SELECT)) {
            sql = buildSelect(script, false, entityInfo);
        } else if (script.startsWith(SQL.COUNT)) {
            sql = buildSelect(script, true, entityInfo);
        } else if (script.startsWith(SQL.UPDATE)) {
            sql = buildUpdateOrDelete(script, true, entityInfo);
        } else if (script.startsWith(SQL.DELETE)) {
            sql = buildUpdateOrDelete(script, false, entityInfo);
        }
        if (sql == null) {
            throw new RuntimeException("unsupported sql script: " + script);
        }
        return super.createSqlSource(configuration, sql, parameterType);
    }

    private static String buildSelect(String script, boolean count, EntityInfo entityInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        String[] split = script.split(" ");
        sb.append("select ");
        if (count) {
            sb.append("count(*)");
        } else {
            sb.append("*");
        }
        sb.append(" from ").append(entityInfo.getTableName());
        append(sb, split);
        sb.append("</script>");
        return sb.toString();
    }

    private static String buildSelectInIds(EntityInfo entityInfo) {
        return "<script>"
              + "select * from " + entityInfo.getTableName()
              + " where " + entityInfo.getIdFieldInfo().getColumn()
              + " in "
              + "<foreach item=\"item\" collection=\"ids\" separator=\",\" open=\"(\" close=\")\">"
              + "#{item}" + "</foreach>"
              + "</script>";
    }

    private static String buildUpdateOrDelete(String script, boolean update,
          EntityInfo entityInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<script>");
        String[] split = script.split(" ");
        if (update) {
            sb.append("update ");
            sb.append(entityInfo.getTableName());
            append(sb, split);
        } else {
            sb.append("delete from ");
            sb.append(entityInfo.getTableName());
            append(sb, split);
        }
        sb.append("</script>");
        return sb.toString();
    }

    private static void append(StringBuilder sb, String[] split) {
        int k = 0;
        for (int i = 1; i < split.length; i++) {
            String s = split[i];
            sb.append(" ");
            if (s.startsWith(IN_BEGIN) && s.endsWith(IN_END)) {
                sb.append("in ");
                String item = "item" + k;
                int begin = s.indexOf(IN_BEGIN) + IN_BEGIN.length();
                int end = s.indexOf(IN_END);
                sb.append("<foreach item=\"").append(item).append("\" collection=\"")
                      .append(s, begin, end).append("\" separator=\",\" open=\"(\" close=\")\">")
                      .append("#{")
                      .append(item).append("}").append("</foreach>");
                k++;
            } else {
                sb.append(s);
            }
        }
    }
}
