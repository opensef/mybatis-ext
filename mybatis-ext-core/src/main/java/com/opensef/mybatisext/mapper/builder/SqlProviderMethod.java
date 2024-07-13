package com.opensef.mybatisext.mapper.builder;

/**
 * SqlProvider中的sql构建工厂注册名称
 */
public enum SqlProviderMethod {

    insert("insert"),
    insertBatch("insertBatch"),
    update("update"),
    updateBatch("updateBatch"),
    updateAll("updateAll"),
    updateAllBatch("updateAllBatch"),
    delete("delete"),
    deleteBatch("deleteBatch"),
    deleteById("deleteById"),
    deleteBatchByIds("deleteBatchByIds"),
    findById("findById"),
    findByLambdaQuery("findByLambdaQuery"),
    findPage("findPage"),
    count("count"),

    ;

    private final String value;

    SqlProviderMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
