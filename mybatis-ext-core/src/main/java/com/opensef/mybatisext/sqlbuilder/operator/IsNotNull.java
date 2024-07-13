package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class IsNotNull implements Operator {

    private final String column;

    public IsNotNull(String column) {
        this.column = column;
    }

    @Override
    public String toSql(Params params) {
        return SqlOperatorUtil.isNotNull(column);
    }

}
