package com.opensef.mybatisext.mapper;

import org.apache.ibatis.type.TypeHandler;

/**
 * 实体属性信息
 */
public class EntityPropertyInfo {

    /**
     * 是否忽略属性
     */
    private Boolean ignore;

    /**
     * 实体属性名称
     */
    private String propertyName;

    /**
     * 数据库字段名称
     */
    private String columnName;

    /**
     * typeHandler
     */
    private Class<? extends TypeHandler<?>> typeHandler;

    public Boolean getIgnore() {
        return ignore;
    }

    public void setIgnore(Boolean ignore) {
        this.ignore = ignore;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Class<? extends TypeHandler<?>> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
        this.typeHandler = typeHandler;
    }

    @Override
    public String toString() {
        return "EntityPropertyInfo{" +
                "ignore=" + ignore +
                ", propertyName='" + propertyName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", typeHandler=" + typeHandler +
                '}';
    }

}
