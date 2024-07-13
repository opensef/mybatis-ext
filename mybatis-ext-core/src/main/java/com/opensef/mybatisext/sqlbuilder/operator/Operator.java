package com.opensef.mybatisext.sqlbuilder.operator;


import com.opensef.mybatisext.sqlbuilder.Params;

/**
 * 运算符接口
 */
public interface Operator {

    String toSql(Params params);

}
