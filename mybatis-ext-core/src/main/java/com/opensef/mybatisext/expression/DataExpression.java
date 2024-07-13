package com.opensef.mybatisext.expression;

/**
 * 表达式信息
 */
public interface DataExpression {

    /**
     * 根据输入的访问者返回对应的sql语句
     *
     * @param visitor 访问者对象
     * @return sql语句
     */
    String accept(DataExpressionVisitor visitor);

}
