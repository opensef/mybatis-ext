package com.opensef.mybatisext.sqlbuilder.operator;

import com.opensef.mybatisext.sqlbuilder.Params;
import com.opensef.mybatisext.sqlbuilder.PlainSelect;
import com.opensef.mybatisext.sqlbuilder.SqlOperatorUtil;

public class Exists implements Operator {

    private final PlainSelect select;

    public Exists(PlainSelect select) {
        this.select = select;
    }

    @Override
    public String toSql(Params params) {
        return SqlOperatorUtil.exists(select, params.getParamMap());
    }

}
