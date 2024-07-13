package com.opensef.mybatisext.expression;

/**
 * 表达式
 */
public enum Condition {

    /**
     * 等于
     */
    EQUALS,

    /**
     * 不等于
     */
    NOT_EQUALS,

    /**
     * 包含
     */
    IN,

    /**
     * 不包含
     */
    NOT_IN,

    /**
     * 大于
     */
    GREATER_THAN,

    /**
     * 大于等于
     */
    GREATER_THAN_EQUALS,

    /**
     * 小于
     */
    LESS_THAN,

    /**
     * 小于等于
     */
    LESS_THAN_EQUALS,

    /**
     * like
     */
    LIKE,

    /**
     * not like
     */
    NOT_LIKE,

    /**
     * left like
     */
    LEFT_LIKE,

    /**
     * not left like
     */
    NOT_LEFT_LIKE,

    /**
     * right like
     */
    RIGHT_LIKE,

    /**
     * not right like
     */
    NOT_RIGHT_LIKE,

    /**
     * between
     */
    BETWEEN,

    /**
     * not between
     */
    NOT_BETWEEN,

}
