package com.opensef.mybatisext.mapper.builder;

import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

/**
 * Repository sql构建器接口
 */
public interface MapperSqlBuilder {

    String SCRIPT_START = "<script>";
    String SCRIPT_END = "</script>";

    /**
     * 构建sql信息，需要使用args时，实现此方法
     *
     * @param entityClass 实体Class
     * @param args        参数，baseMapper、SqlProvider和此处的参数顺序必须相同
     * @return MapperSql
     */
    MapperSql build(Class<?> entityClass, Object... args);

    /**
     * 组装参数占位符
     *
     * @param fieldName 属性名
     * @return 参数占位符
     */
    default String paramPlaceholder(String fieldName) {
        return "#{" + fieldName + "}";
    }

    /**
     * 组装参数占位符
     *
     * @param fieldName 属性名
     * @return 参数占位符
     */
    default String paramPlaceholder(String fieldName, Class<? extends TypeHandler<?>> typeHandler) {
        if (null == typeHandler || typeHandler.equals(UnknownTypeHandler.class)) {
            return "#{" + fieldName + "}";
        }
        return "#{" + fieldName + ",typeHandler=" + typeHandler.getName() + "}";
    }

    /**
     * 组装 列名 = 属性名 表达式
     *
     * @param column    列名
     * @param fieldName 属性名
     * @return 表达式
     */
    default String condition(String column, String fieldName) {
        return column + "=" + "#{" + fieldName + "}";
    }

    /**
     * 组装 列名 = 属性名 表达式
     *
     * @param column    列名
     * @param fieldName 属性名
     * @return 表达式
     */
    default String condition(String column, String fieldName, Class<? extends TypeHandler<?>> typeHandler) {
        return column + "=" + paramPlaceholder(fieldName, typeHandler);
    }

    /**
     * 组装 动态 列名 = 属性名 表达式
     *
     * @param column    列名
     * @param fieldName 属性名
     * @return 带有if的表达式，例：<br>
     * &lt; if test= "name != null"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp; name = #{name}<br>
     * &lt;/if&gt;
     */
    default String ifCondition(String column, String fieldName) {
        return String.format("<if test=\"%s  != null\">\n", fieldName) + condition(column, fieldName) + "\n</if>";
    }

    /**
     * 组装 动态 列名 = 属性名 表达式
     *
     * @param column    列名
     * @param fieldName 属性名
     * @return 带有if的表达式，例：<br>
     * &lt; if test= "name != null"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp; name = #{name}<br>
     * &lt;/if&gt;
     */
    default String ifCondition(String column, String fieldName, Class<? extends TypeHandler<?>> typeHandler) {
        return String.format("<if test=\"%s  != null\">\n", fieldName) + condition(column, fieldName, typeHandler) + "\n</if>";
    }

    /**
     * 组装 动态 列名 = 属性名 表达式
     *
     * @param column    列名
     * @param fieldName 属性名
     * @param suffix    后缀
     * @return 带有if的表达式，例：<br>
     * &lt; if test= "name != null"&gt;<br>
     * &nbsp;&nbsp;&nbsp;&nbsp; name = #{name}<br>
     * &lt;/if&gt;
     */
    default String ifCondition(String column, String fieldName, Class<? extends TypeHandler<?>> typeHandler, String suffix) {
        return String.format("<if test=\"%s  != null\">\n", fieldName) + condition(column, fieldName, typeHandler) + suffix + "\n</if>";
    }

    /**
     * 组装script标签
     *
     * @param sql sql
     * @return 带script标签的 sql
     */
    default String script(String sql) {
        return SCRIPT_START + sql + SCRIPT_END;
    }

}
