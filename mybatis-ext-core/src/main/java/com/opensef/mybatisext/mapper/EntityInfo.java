package com.opensef.mybatisext.mapper;

import org.apache.ibatis.type.TypeHandler;

import java.util.List;

public class EntityInfo {

    /**
     * 表名
     */
    private String tableName;

    /**
     * ID属性名，一个实体只能有一个ID，不支持多个
     */
    private String fieldIdName;

    /**
     * ID列名，一张表只能有一个主键字段，不支持多个
     */
    private String columnIdName;

    /**
     * 是否逻辑删除
     */
    private Boolean logicDelete;

    /**
     * 逻辑删除字段属性名
     */
    private String fieldDeletedName;

    /**
     * 逻辑删除字段列名
     */
    private String columnDeletedName;

    /**
     * 逻辑删除自动填充属性
     */
    private List<FieldInfo> autoFillLogicDeleteFieldInfoList;

    /**
     * 全部属性信息（不包括忽略的属性）
     */
    private List<FieldInfo> fieldInfoList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldIdName() {
        return fieldIdName;
    }

    public void setFieldIdName(String fieldIdName) {
        this.fieldIdName = fieldIdName;
    }

    public String getColumnIdName() {
        return columnIdName;
    }

    public void setColumnIdName(String columnIdName) {
        this.columnIdName = columnIdName;
    }

    public Boolean getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(Boolean logicDelete) {
        this.logicDelete = logicDelete;
    }

    public String getFieldDeletedName() {
        return fieldDeletedName;
    }

    public void setFieldDeletedName(String fieldDeletedName) {
        this.fieldDeletedName = fieldDeletedName;
    }

    public String getColumnDeletedName() {
        return columnDeletedName;
    }

    public void setColumnDeletedName(String columnDeletedName) {
        this.columnDeletedName = columnDeletedName;
    }

    public List<FieldInfo> getAutoFillLogicDeleteFieldInfoList() {
        return autoFillLogicDeleteFieldInfoList;
    }

    public void setAutoFillLogicDeleteFieldInfoList(List<FieldInfo> autoFillLogicDeleteFieldInfoList) {
        this.autoFillLogicDeleteFieldInfoList = autoFillLogicDeleteFieldInfoList;
    }

    public List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }

    public static class FieldInfo {

        /**
         * 是否忽略属性
         */
        private Boolean ignore;

        /**
         * 是否指定了TableColumn注解
         */
        private Boolean tableColumn;

        /**
         * 实体属性名称
         */
        private String fieldName;

        /**
         * typeHandler
         */
        private Class<? extends TypeHandler<?>> typeHandler;

        /**
         * 数据库字段名称
         */
        private String columnName;

        /**
         * 是否是ID
         */
        private Boolean idField;

        /**
         * 是否逻辑删除
         */
        private Boolean logicDelete;

        /**
         * 逻辑删除时是否自动赋值
         */
        private Boolean autoFillLogicDelete;

        public Boolean getIgnore() {
            return ignore;
        }

        public void setIgnore(Boolean ignore) {
            this.ignore = ignore;
        }

        public Boolean getTableColumn() {
            return tableColumn;
        }

        public void setTableColumn(Boolean tableColumn) {
            this.tableColumn = tableColumn;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public Class<? extends TypeHandler<?>> getTypeHandler() {
            return typeHandler;
        }

        public void setTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
            this.typeHandler = typeHandler;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public Boolean getIdField() {
            return idField;
        }

        public void setIdField(Boolean idField) {
            this.idField = idField;
        }

        public Boolean getLogicDelete() {
            return logicDelete;
        }

        public void setLogicDelete(Boolean logicDelete) {
            this.logicDelete = logicDelete;
        }

        public Boolean getAutoFillLogicDelete() {
            return autoFillLogicDelete;
        }

        public void setAutoFillLogicDelete(Boolean autoFillLogicDelete) {
            this.autoFillLogicDelete = autoFillLogicDelete;
        }

    }

}
