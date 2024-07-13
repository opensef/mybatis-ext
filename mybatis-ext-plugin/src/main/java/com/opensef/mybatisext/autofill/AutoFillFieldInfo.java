package com.opensef.mybatisext.autofill;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 自动填充属性信息
 */
public class AutoFillFieldInfo {

    /**
     * id Field
     */
    private Field idField;

    /**
     * 逻辑删除Field
     */
    private Field logicDeletedField;

    /**
     * 新增-自动填充的全部Field
     */
    private List<Field> insertAutoFillFieldList;

    /**
     * 修改-自动填充的全部Field
     */
    private List<Field> updateAutoFillFieldList;

    /**
     * 逻辑删除时，自动填充的Field
     */
    private List<Field> logicDeleteAutoFillFieldList;

    public Field getIdField() {
        return idField;
    }

    public void setIdField(Field idField) {
        this.idField = idField;
    }

    public Field getLogicDeletedField() {
        return logicDeletedField;
    }

    public void setLogicDeletedField(Field logicDeletedField) {
        this.logicDeletedField = logicDeletedField;
    }

    public List<Field> getInsertAutoFillFieldList() {
        return insertAutoFillFieldList;
    }

    public void setInsertAutoFillFieldList(List<Field> insertAutoFillFieldList) {
        this.insertAutoFillFieldList = insertAutoFillFieldList;
    }

    public List<Field> getUpdateAutoFillFieldList() {
        return updateAutoFillFieldList;
    }

    public void setUpdateAutoFillFieldList(List<Field> updateAutoFillFieldList) {
        this.updateAutoFillFieldList = updateAutoFillFieldList;
    }

    public List<Field> getLogicDeleteAutoFillFieldList() {
        return logicDeleteAutoFillFieldList;
    }

    public void setLogicDeleteAutoFillFieldList(List<Field> logicDeleteAutoFillFieldList) {
        this.logicDeleteAutoFillFieldList = logicDeleteAutoFillFieldList;
    }

}
