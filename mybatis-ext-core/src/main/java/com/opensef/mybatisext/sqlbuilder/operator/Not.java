package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class Not implements Operator {

    public Not() {

    }

    @Override
    public String toSql(Params params) {
        return SqlOperatorUtil.not();
    }

}
