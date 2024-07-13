package com.opensef.mybatisext.sqlbuilder;

/**
 * 逻辑运算符
 */
public enum SqlLogicOperator {

    /**
     * 与
     */
    AND(" and "),

    /**
     * 或
     */
    OR(" or ");

    private final String code;

    SqlLogicOperator(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
