package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class NotBetween implements Operator {

    private final String column;
    private final Object startValue;
    private final Object endValue;

    public NotBetween(String column, Object startValue, Object endValue) {
        this.column = column;
        this.startValue = startValue;
        this.endValue = endValue;
    }

    @Override
    public String toSql(Params params) {
        return SqlOperatorUtil.notBetween(column, startValue, endValue, params.getParamMap());
    }

}
