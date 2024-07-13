package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class IsNull implements Operator {

    private final String column;

    public IsNull(String column) {
        this.column = column;
    }

    @Override
    public String toSql(Params params) {
        return SqlOperatorUtil.isNull(column);
    }

}
