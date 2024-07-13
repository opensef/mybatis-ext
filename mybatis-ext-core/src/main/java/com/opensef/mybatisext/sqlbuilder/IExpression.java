package com.opensef.mybatisext.sqlbuilder;

/**
 * 表达式接口
 */
public interface IExpression {

    /**
     * 生成表达式sql
     *
     * @return sql
     */
    String toSql();

    /**
     * 获取表达式中的参数
     *
     * @return 参数
     */
    Params getParams();

}
